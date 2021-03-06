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

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HttpRestProtocolServerHandler extends SimpleChannelInboundHandler<HttpObject> implements RestProcessor.Response {

    private String path;
    private QueryStringDecoder queryStringDecoder;
    private RestServiceProtocol.MethodType methodType;
    private String url;
    private List<Message.Value> headers;
    private long position;

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
    ByteBuf content;

    private Reply reply;

    ProcessorLocator.Locator locator;
    JsonBuilder jsonBuilder = new JsonBuilderImpl();

    private Message.MessageName messageName;
    private Iterable<Message.Value> values;

    private StandardProtocol.ErrorClass errorClass;
    private StandardProtocol.ErrorKind errorKind;

    public HttpRestProtocolServerHandler(ServerCore serverCore, ProcessorLocator.Locator locator) {
        this.serverCore = serverCore;
        this.locator = locator;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    RestServiceProtocol.MethodType translate(HttpMethod httpMethod) {
        if (HttpMethod.GET.equals(httpMethod)) return RestServiceProtocol.MethodType.GET;
        if (HttpMethod.POST.equals(httpMethod)) return RestServiceProtocol.MethodType.POST;
        if (HttpMethod.PUT.equals(httpMethod)) return RestServiceProtocol.MethodType.PUT;
        if (HttpMethod.DELETE.equals(httpMethod)) return RestServiceProtocol.MethodType.DELETE;
        throw new RuntimeException("Internal error!");
    }

    Task createProtocolTask() {
        if (this.content.readableBytes()==0) {
            return serverCore.createTestUser("test").createRestServiceProtocol()
                    .httpSimpleCall(methodType, url, headers);
        } else {
            return serverCore.createTestUser("test").createRestServiceProtocol()
                    .httpSimpleCall(methodType, url, headers);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            synchronized (this) {
                reply = Reply.Waiting;
            }
            HttpRequest request = this.request = (HttpRequest) msg;

            url = request.getUri();
            content = Unpooled.buffer();
            methodType = translate(((HttpRequest) msg).getMethod());
            headers = this.request.headers().entries().stream()
                    .map(e -> MessageValue.text(NamedField.text(e.getKey()), e.getValue()))
                    .collect(Collectors.toList());
            position = 0L;
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            this.content.writeBytes(httpContent.content());

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;

                System.out.println("HttpProtocolSessionServerHandler.channelRead0 *************************** CONTENT >>>>>>>>>>>>>>>>>>>>");
                System.out.println("readableBytes="+this.content.readableBytes());
                System.out.println("HttpProtocolSessionServerHandler.channelRead0 *************************** CONTENT <<<<<<<<<<<<<<<<<<<<<");

                try {
                    final Task task = createProtocolTask();
                    task.addSuccessAction((messageName1, values1) -> {

                    });
                    task.addFailedAction((errorClass1, errorKind1, values1) -> {

                    });
                    task.start();

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

                } catch (RuntimeException e) {
                    System.out.println("HttpProtocolSessionServerHandler.channelRead0 ERROR "+e.getMessage() + "%%%%%%%%%%%%%%%%%%%%%%%%");
                    e.printStackTrace();
                    failed(ErrorClass.UNKNOWN, ErrorKind.UNKNOWN_TARGET, Arrays.asList());
                }

                System.out.println("HttpProtocolSessionServerHandler.channelRead0 RELEASE Content ----------------------------------------");
                this.content.release();
                this.content = null;

                ByteBuf replyContent = Unpooled.copiedBuffer(
                        errorClass != null ?
                            errorToString() :
                            msgToString(),
                        CharsetUtil.UTF_8);

                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, trailer.getDecoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                        replyContent);
                //exec.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");

                boolean keepAlive = HttpHeaders.isKeepAlive(request);

                if (keepAlive) {
                    response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
                    response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
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

            @Override
            public void write(Message.Field field, Map<? extends Name, ? extends Message.Value> map) {
                throw new RuntimeException("Not supported!");
            }

            @Override public <V> void writeAny(Message.Field field, V value) {
                item.setChildValue(field.getName().toString(), ""+value);
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
        private Map<Integer,Address> storedFoundAlias = new HashMap<>();
        private int level = 0;

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
        public Stream<Entry<Integer, Address>> getStoredFoundAlias() {
            return storedFoundAlias.entrySet().stream();
        }

        @Override
        public void foundAlias(String value, Address foundAlias) {
            this.storedFoundAlias.put(level,foundAlias);
        }

        @Override
        public ProcessorLocator.LocatorContext next() {
            level++;
            return this;
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
            return createAddressFromText(value, field);
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

        @Override
        public String text() {
            return value;
        }

        @Override
        public Message.Value value() {
            return field.createValueFromString(value);
        }
    }

    private class LocalPathItem implements RestProcessor.PathItem {
        private final String s;

        public LocalPathItem(String s) {
            this.s = s;
        }

        @Override
        public <A extends Address> A address(Message.Field field) {
            return new RestProcessorRequestValue(field, s).address();
        }

        @Override
        public <N extends Name> N name(Message.Field field) {
            return new RestProcessorRequestValue(field, s).name();
        }
    }

    private RestProcessor.PathItem createAddressPathItem(Address address) { return new AddressPathItem(address);}

    private class AddressPathItem implements RestProcessor.PathItem {
        private final Address address;

        public AddressPathItem(Address address) {
            this.address = address;
        }

        @Override
        public <A extends Address> A address(Message.Field field) {
            return (A) address;
        }

        @Override
        public <N extends Name> N name(Message.Field field) {
            throw new RuntimeException("Not supported");
        }
    }

    private <A extends Address> A createAddressFromText(String value, Message.Field field) {
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

    private class MyRequest implements RestProcessor.Request {
        private final RestProcessor.Operation operation;
        private final List<RestProcessor.PathItem> pathItems;
        private final ProcessorLocator.LocatorResponse locatorResponse;
        private final QueryStringDecoder queryStringDecoder;

        public MyRequest(RestProcessor.Operation operation, List<RestProcessor.PathItem> pathItems, ProcessorLocator.LocatorResponse locatorResponse, QueryStringDecoder queryStringDecoder) {
            this.operation = operation;
            this.pathItems = pathItems;
            this.locatorResponse = locatorResponse;
            this.queryStringDecoder = queryStringDecoder;
        }

        @Override
        public RestProcessor.Operation getOperation() {
            return operation;
        }

        @Override
        public Optional<RestProcessor.PathItem> getPathItem(int index) {
            RestProcessor.PathItem pathItem1 = pathItems.get(index);
            try {
                RestProcessor.PathItem pathItem = locatorResponse.foundMatchingAlias(index)
                        .map(address -> createAddressPathItem(address))
                        .orElse(pathItem1);
                return Optional.of(pathItem);
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
        public Optional<RestProcessor.RequestValue> getValueOptional(Message.Field field) {
            String param = params.get(field.getName().toString());
            if (param==null)
                return Optional.empty();
            return Optional.of(new RestProcessorRequestValue(field, param));
        }

        @Override
        public byte[] getContent() {
            byte[] bytes = new byte[content.readableBytes()];
            content.readBytes(bytes);
            return bytes;
        }

    }
}
