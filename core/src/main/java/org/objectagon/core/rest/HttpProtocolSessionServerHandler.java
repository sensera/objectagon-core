package org.objectagon.core.rest;

/**
 * Created by christian on 2016-02-11.
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.MessageValueMessage;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.object.*;
import org.objectagon.core.object.field.FieldIdentityImpl;
import org.objectagon.core.object.field.FieldNameImpl;
import org.objectagon.core.object.instance.InstanceIdentityImpl;
import org.objectagon.core.object.instanceclass.InstanceClassIdentityImpl;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.object.relation.RelationIdentityImpl;
import org.objectagon.core.object.relationclass.RelationClassIdentityImpl;
import org.objectagon.core.object.relationclass.RelationNameImpl;
import org.objectagon.core.rest.util.JsonBuilder;
import org.objectagon.core.rest.util.JsonBuilderImpl;
import org.objectagon.core.task.Task;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpProtocolSessionServerHandler extends SimpleChannelInboundHandler<HttpObject> implements RestProcessor.Response {

    private final Consumer<Entry<String, String>> entryConsumer = entry -> System.out.println("  " + entry.getKey() + "=" + entry.getValue());

    private enum TaskName implements Task.TaskName {
        REGISTER_NAME, LOOKUP,
    }

    private enum Reply {
        Waiting,
        Success,
        Failed
    }

    private ServerCore serverCore;
    private HttpRequest request;
    Map<String,String> params = new HashMap<>();

    private Reply reply;

    ProcessorLocator.Locator locator;
    JsonBuilder jsonBuilder = new JsonBuilderImpl();

    private Message.MessageName messageName;
    private Iterable<Message.Value> values;

    private StandardProtocol.ErrorClass errorClass;
    private StandardProtocol.ErrorKind errorKind;

    public HttpProtocolSessionServerHandler(ServerCore serverCore, ProcessorLocator.Locator locator) {
        this.serverCore = serverCore;
        this.locator = locator;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    RestProcessor.Operation translate(HttpMethod httpMethod) {
        if (HttpMethod.GET.equals(httpMethod)) return RestProcessor.Operation.Get;
        if (HttpMethod.POST.equals(httpMethod)) return RestProcessor.Operation.UpdateExecute;
        if (HttpMethod.PUT.equals(httpMethod)) return RestProcessor.Operation.SaveNew;
        if (HttpMethod.DELETE.equals(httpMethod)) return RestProcessor.Operation.Delete;
        throw new RuntimeException("Internal error!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            synchronized (this) {
                reply = Reply.Waiting;
            }
            HttpRequest request = this.request = (HttpRequest) msg;
            //if (!request.getMethod().equals(HttpMethod.GET))
                //return;
            URI uri = new URI(request.getUri());
            String path = uri.getPath();
            System.out.println("HttpProtocolSessionServerHandler.channelRead0 "+request.getMethod()+" "+path+" -----------------------------------------------------------------------------------------------------------------------------------------------------");
            //System.out.println("HttpProtocolSessionServerHandler.channelRead0 "+path);
            params = new HashMap<>();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            queryStringDecoder.parameters().forEach((name, values) -> {
                //System.out.println("  " + name);
                //values.stream().forEach(value -> System.out.println("  - " + value));
                params.put(name, values.get(0));
            });

            try {
                String[] splittedPath = path.length() == 0 ? new String[0] : path.substring(1).split("/");
                List<RestProcessor.PathItem> pathItems = new ArrayList<>();
                Stream.of(splittedPath).forEach(s -> pathItems.add(new RestProcessor.PathItem() {
                    @Override
                    public <A extends Address> A address(Message.Field field) {
                        return new RestProcessorRequestValue(field, s).address();
                    }

                    @Override
                    public <N extends Name> N name(Message.Field field) {
                        return new RestProcessorRequestValue(field, s).name();
                    }
                }));
                final RestProcessor.Operation operation = translate(((HttpRequest) msg).getMethod());

                ProcessorLocator.LocatorResponse locatorResponse = locator.match(new LocalLocatorContext(Arrays.asList(splittedPath).iterator(), operation, serverCore.createTestUser("test") ));
                Optional<RestProcessor> match = locatorResponse.restProcessor();
                RestProcessor.Request req = new RestProcessor.Request() {
                    @Override
                    public RestProcessor.Operation getOperation() {
                        return operation;
                    }

                    @Override
                    public Optional<RestProcessor.PathItem> getPathItem(int index) {
                        try {
                            return Optional.of(pathItems.get(index));
                        } catch (Exception e) {
                            return Optional.empty();
                        }
                    }

                    @Override
                    public String getUser() {
                        return "test";
                    }

                    @Override
                    public Message.Value[] queryAsValues() {
                        List<Message.Value> resp = new ArrayList<>();
                        queryStringDecoder.parameters().forEach((name, values) -> {
                            resp.add(MessageValue.text(NamedField.text(name),values.get(0)));
                        });
                        return resp.toArray(new Message.Value[resp.size()]);
                    }

                    @Override
                    public RestProcessor.RequestValue getValue(Message.Field field) {
                        String param = params.get(field.getName().toString());
                        if (param==null)
                            throw new RuntimeException("No param named "+field.getName().toString());
                        return new RestProcessorRequestValue(field, param);
                    }

                    @Override
                    public Optional<Address> getAlias() {
                        return locatorResponse.foundMatchingAlias();
                    }
                };
                match.ifPresent(restProcessor -> restProcessor.process(serverCore, req, this));
                match.orElseThrow(() -> new RuntimeException("No match for path '"+path+"'"));
            } catch (RuntimeException e) {
                System.out.println("HttpProtocolSessionServerHandler.channelRead0 ERROR "+e.getMessage() + "%%%%%%%%%%%%%%%%%%%%%%%%");
                e.printStackTrace();
                failed(ErrorClass.UNKNOWN, ErrorKind.UNKNOWN_TARGET, Arrays.asList());
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;

                synchronized (this) {
                    //System.out.println("HttpProtocolSessionServerHandler.channelRead0 WAITING");
                    if (reply.equals(Reply.Waiting))
                        wait(1000);
                    //System.out.println("HttpProtocolSessionServerHandler.channelRead0 WAITING released");
                    if (reply.equals(Reply.Waiting)) {
                        errorClass = ErrorClass.PROTOCOL;
                        errorKind = ErrorKind.TIMEOUT;
                    }
                }

                ByteBuf content = Unpooled.copiedBuffer(
                        errorClass != null ?
                            errorToString() :
                            msgToString(),
                        CharsetUtil.UTF_8);

                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1, trailer.getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
                        content);
                //response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");

                boolean keepAlive = HttpHeaders.isKeepAlive(request);

                if (keepAlive) {
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                    response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    //System.out.println("HttpSnoopServerHandler.channelRead0 KEEP ALIVE!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                channelHandlerContext.write(response);
                if (!keepAlive) {
                    channelHandlerContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                    //System.out.println("HttpSnoopServerHandler.channelRead0 ---------------------------------------------------------------");
                }
            }
        }

    }

    @Override
    public RestProcessor.JsonBuilder createJsonBuilder(String name) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public void error(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        System.out.println("HttpProtocolSessionServerHandler.error errorClass="+errorClass+" errorKind="+errorKind);
        failed(errorClass, errorKind, values);
    }

    @Override
    public void reply(Message.MessageName messageName, Iterable<Message.Value> values) {
        Message.MessageName originalMessageName = MessageValueFieldUtil.create(values).getValueByFieldOption(StandardProtocol.FieldName.ORIGINAL_MESSAGE).map(Message.Value::asMessageName).orElse(null);
        System.out.println("HttpProtocolSessionServerHandler.reply messageName="+messageName+" original message "+originalMessageName);
        success(messageName, values);
    }

    private synchronized void success(Message.MessageName messageName, Iterable<Message.Value> values) {
        if (reply==null)
            throw new RuntimeException("reply status is null!");
        if (!Reply.Waiting.equals(reply))
            throw new RuntimeException("Reply status is "+reply);
        this.messageName = messageName;
        this.values = values;
        this.reply = Reply.Success;
        this.notifyAll();
    }

    private synchronized void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        if (reply==null)
            throw new RuntimeException("reply status is null!");
        if (!Reply.Waiting.equals(reply))
            throw new RuntimeException("Reply status is "+reply);
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.values = values;
        this.reply = Reply.Failed;
        this.notifyAll();
    }

    private String errorToString() {
        List<Message.Value> values = Arrays.asList(
                MessageValue.text(StandardField.ERROR_CLASS, errorClass.name()),
                MessageValue.text(StandardField.ERROR_KIND, errorKind.name()),
                MessageValue.values(StandardField.VALUES, this.values)
        );
        JsonBuilder.Builder builder = jsonBuilder.builder();
        StreamSupport.stream(values.spliterator(),false).forEach(v -> valueToBuilder(builder, v));
        return builder.build().toString();
    }

    private String msgToString() {
        JsonBuilder.Builder builder = jsonBuilder.builder();
        StreamSupport.stream(values.spliterator(),false).forEach(v -> valueToBuilder(builder, v));
        return builder.build().toString();
    }

    private void valueToBuilder(JsonBuilder.Item item, Message.Value value) {
        value.writeTo(new Message.Writer() {
            @Override
            public void write(Message.Field field, String text) {
                item.setChildValue(field.getName().toString(), text);
            }

            @Override
            public void write(Message.Field field, Long number) {
                item.setChildValue(field.getName().toString(), ""+number);
            }

            @Override
            public void write(Message.Field field, MessageValueMessage message) {
                JsonBuilder.Item child = item.addChild(field.getName().toString());
                child.setChildValue("message", ""+message.getMessageName());
                StreamSupport.stream(message.getValues().spliterator(),false).forEach(v -> valueToBuilder(child.addChild("values"), v));
            }

            @Override
            public void write(Message.Field field, Message.MessageName messageName) {
                item.setChildValue(field.getName().toString(), ""+messageName);
            }

            @Override
            public void write(Message.Field field, Address address) {
                address.toValue(item.addChild(field.getName().toString()));
            }

            @Override
            public void write(Message.Field field, Name name) {
                item.setChildValue(field.getName().toString(), ""+name);
            }

            @Override
            public void write(Message.Field field, Message.Values values) {
                StreamSupport.stream(values.values().spliterator(),false).forEach(v -> valueToBuilder(item.addChild(field.getName().toString()), v));
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static class LocalLocatorContext implements ProcessorLocator.LocatorContext {
        private final Iterator<String> path;
        private final RestProcessor.Operation operation;
        private final ServerCore.TestUser testUser;
        private Address storedFoundAlias;

        public LocalLocatorContext(Iterator<String> path, RestProcessor.Operation operation, ServerCore.TestUser testUser) {
            this.path = path;
            this.operation = operation;
            this.testUser = testUser;
        }

        @Override
        public Iterator<String> path() {
            return path;
        }

        @Override
        public RestProcessor.Operation operation() {
            return operation;
        }

        @Override
        public Optional<Address> findAlias(String name) {
            return testUser.getValue(NamedField.address(name)).map(Message.Value::asAddress);
        }

        @Override
        public Optional<Address> getStoredFoundAlias() {
            return Optional.ofNullable(storedFoundAlias);
        }

        @Override
        public void foundAlias(String value, Address foundAlias) {
            this.storedFoundAlias = foundAlias;
        }
    }

    private class RestProcessorRequestValue implements RestProcessor.RequestValue {
        Message.Field field;
        String value;

        public RestProcessorRequestValue(Message.Field field, String value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public <A extends Address> A address() {
            String[] splittedAddress = value.split("-");
            long timestamp = Long.parseLong(splittedAddress[0]);
            long addressId = Long.parseLong(splittedAddress[1]);
            if (Field.FIELD_IDENTITY.equals(field))
                return (A) new FieldIdentityImpl(null, serverCore.serverId, timestamp, addressId);
            if (RelationClass.RELATION_CLASS_IDENTITY.equals(field))
                return (A) new RelationClassIdentityImpl(null, null, null, serverCore.serverId, timestamp, addressId);
            if (Relation.RELATION_IDENTITY.equals(field))
                return (A) new RelationIdentityImpl(null, null, null, serverCore.serverId, timestamp, addressId);
            if (InstanceClass.INSTANCE_CLASS_IDENTITY.equals(field))
                return (A) new InstanceClassIdentityImpl(serverCore.serverId, timestamp, addressId);
            if (Instance.INSTANCE_IDENTITY.equals(field))
                return (A) new InstanceIdentityImpl(null, serverCore.serverId, timestamp, addressId);
            return (A) StandardAddress.standard(serverCore.serverId, timestamp, addressId);
        }

        @Override
        public <N extends Name> N name() {
            if (Field.FIELD_NAME.equals(field))
                return (N) FieldNameImpl.create(value);
            if (InstanceClass.INSTANCE_CLASS_NAME.equals(field))
                return (N) InstanceClassNameImpl.create(value);
            if (RelationClass.RELATION_NAME.equals(field))
                return (N) RelationNameImpl.create(value);
            return (N) StandardName.name(value);
        }
    }
}
