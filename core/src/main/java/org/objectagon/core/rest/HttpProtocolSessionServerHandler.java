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
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.Util;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
        throw new RuntimeException("INternal error!");
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
            System.out.println("HttpProtocolSessionServerHandler.channelRead0 "+request.getMethod()+" "+path+" --------------------------------------------------");
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
/*
                for (int i = 0; i < splittedPath.length; i++) {
                    splittedPath[i] = splittedPath[i].replace("/","");
                }
*/
                List<RestProcessor.PathItem> pathItems = new ArrayList<>();
                Stream.of(splittedPath).forEach(s -> pathItems.add(new RestProcessor.PathItem() {
                    @Override
                    public <A extends Address> A address() {
                        String[] splittedAddress = s.split("-");
                        return (A) StandardAddress.standard(serverCore.serverId, Long.parseLong(splittedAddress[0]), Long.parseLong(splittedAddress[1]));
                    }

                    @Override
                    public <N extends Name> N name() {
                        return (N) StandardName.name(s);
                    }
                }));
                Optional<RestProcessor> match = locator.match(Arrays.asList(splittedPath).iterator(), translate(((HttpRequest) msg).getMethod()) );
                RestProcessor.Request req = new RestProcessor.Request() {
                    @Override
                    public RestProcessor.Operation getOperation() {
                        return translate(((HttpRequest) msg).getMethod());
                    }

                    @Override
                    public Optional<RestProcessor.PathItem> getPathItem(int index) {
                        System.out.println("HttpProtocolSessionServerHandler.getPathItem NOT IMPLEMETED "+index);
                        return Optional.empty();
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
                };
                match.ifPresent(restProcessor -> restProcessor.process(serverCore, req, this));
                match.orElseThrow(() -> new RuntimeException("No match for path '"+path+"'"));
            } catch (RuntimeException e) {
                System.out.println("HttpProtocolSessionServerHandler.channelRead0 ERROR "+e.getMessage() + "%%%%%%%%%%%%%%%%%%%%%%%%");
                failed(ErrorClass.UNKNOWN, ErrorKind.UNKNOWN_TARGET, Arrays.asList());
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;

                synchronized (this) {
                    System.out.println("HttpProtocolSessionServerHandler.channelRead0 WAITING");
                    if (reply.equals(Reply.Waiting))
                        wait(1000);
                    System.out.println("HttpProtocolSessionServerHandler.channelRead0 WAITING released");
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
                response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

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
        System.out.println("HttpProtocolSessionServerHandler.reply messageName="+messageName);
        success(messageName, values);
    }

    private synchronized void success(Message.MessageName messageName, Iterable<Message.Value> values) {
        this.messageName = messageName;
        this.values = values;
        this.reply = Reply.Success;
        this.notifyAll();
    }

    private synchronized void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.values = values;
        this.reply = Reply.Failed;
        this.notifyAll();
    }

    private String errorToString() {
        StringBuilder sb = new StringBuilder("Class: "+errorClass);
        sb.append("\nKind: ").append(errorKind);
        if (values!=null)
            sb.append(Util.printValuesToString(values));
        return sb.toString();
    }

    private String msgToString() {
        StringBuilder sb = new StringBuilder(messageName.toString());
        if (values!=null)
            sb.append(Util.printValuesToString(values));
        return sb.toString();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
