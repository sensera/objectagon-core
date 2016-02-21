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
import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.task.StandardTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpProtocolSessionServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Consumer<Entry<String, String>> entryConsumer = entry -> System.out.println("  " + entry.getKey() + "=" + entry.getValue());

    private enum TaskName implements Task.TaskName {
        REGISTER_NAME, LOOKUP,
    }

    private enum Reply {
        Waiting,
        Success,
        Failed
    }

    private Server server;
    private Address nameServiceAddress;
    private HttpRequest request;
    Map<String,String> params = new HashMap<>();

    private Reply reply;

    private Message.MessageName messageName;
    private Iterable<Message.Value> values;

    private ErrorClass errorClass;
    private ErrorKind errorKind;

    public HttpProtocolSessionServerHandler(Server server, Address nameServiceAddress) {
        this.server = server;
        this.nameServiceAddress = nameServiceAddress;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            synchronized (this) {
                reply = Reply.Waiting;
            }
            HttpRequest request = this.request = (HttpRequest) msg;
            if (!request.getMethod().equals(HttpMethod.GET))
                return;
            URI uri = new URI(request.getUri());
            String path = uri.getPath();
            System.out.println("HttpProtocolSessionServerHandler.channelRead0 "+path);
            params = new HashMap<>();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            queryStringDecoder.parameters().forEach((name, values) -> {
                System.out.println("  " + name);
                values.stream().forEach(value -> System.out.println("  - " + value));
                params.put(name, values.get(0));
            });

            long timestamp = Long.parseLong(params.getOrDefault("timestamp", "10"));
            long id = Long.parseLong(params.getOrDefault("id", "10"));
            String name = params.getOrDefault("name", "Berra");

            if (path.equals("/protocol/name/register")) {
                System.out.println("HttpProtocolSessionServerHandler.channelRead0 TaskName.REGISTER_NAME");
                TaskBuilder taskBuilder = server.getTaskBuilder();
                StandardTask.SendMessageAction<NameServiceProtocol.Send> sendSendMessageAction =
                        send -> send.registerName(StandardAddress.standard(server.getServerId(), timestamp, id), StandardName.name(name));
                taskBuilder.message(TaskName.REGISTER_NAME, NameServiceProtocol.NAME_SERVICE_PROTOCOL, nameServiceAddress, sendSendMessageAction)
                        .success(this::success)
                        .failed(this::failed)
                        .start();
            }
            if (path.equals("/protocol/name/lookup")) {
                TaskBuilder taskBuilder = server.getTaskBuilder();
                StandardTask.SendMessageAction<NameServiceProtocol.Send> sendSendMessageAction =
                        send -> send.lookupAddressByName(StandardName.name(name));
                taskBuilder.message(TaskName.LOOKUP, NameServiceProtocol.NAME_SERVICE_PROTOCOL, nameServiceAddress, sendSendMessageAction)
                        .success(this::success)
                        .failed(this::failed)
                        .start();
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;

                synchronized (this) {
                    if (reply.equals(Reply.Waiting))
                        wait(1000);
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

    private synchronized void success(Message.MessageName messageName, Iterable<Message.Value> values) {
        this.messageName = messageName;
        this.values = values;
        this.notifyAll();
    }

    private synchronized void failed(ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> values) {
        this.errorClass = errorClass;
        this.errorKind = errorKind;
        this.values = values;
        this.notifyAll();
    }

    private String errorToString() {
        StringBuilder sb = new StringBuilder("Class: "+errorClass);
        sb.append("\nKind: ").append(errorKind);
        if (values!=null)
            for (Message.Value value : values) sb.append("\n").append(value.getField().getName()).append("=").append(value.asText());
        return sb.toString();
    }

    private String msgToString() {
        StringBuilder sb = new StringBuilder(messageName.toString());
        if (values!=null)
            for (Message.Value value : values) sb.append("\n").append(value.getField().getName()).append("=").append(value.asText());
        return sb.toString();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
