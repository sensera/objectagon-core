package org.objectagon.core.rest;

/**
 * Created by christian on 2016-02-11.
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.Map.Entry;
import java.util.function.Consumer;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class HttpSnoopServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Consumer<Entry<String, String>> entryConsumer = entry -> System.out.println("  " + entry.getKey() + "=" + entry.getValue());

    private HttpRequest request;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        //System.out.println("HttpSnoopServerHandler.channelRead0 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
//            System.out.println("HttpSnoopServerHandler.channelRead0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            System.out.println("Method: " + request.getMethod());
//            System.out.println("Uri: " + request.getUri());
//            System.out.println("ProtocolVersion: " + request.getProtocolVersion());
//            System.out.println("Headers:");
            request.headers().forEach(entryConsumer);
//            System.out.println("Query String");
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
//            queryStringDecoder.parameters().forEach((name, values) -> {
//                System.out.println("  " + name);
//                values.stream().forEach(value -> System.out.println("  - " + value));
//            });
            if (msg.getDecoderResult().isFailure())
                System.out.println("Failure: "+msg.getDecoderResult().cause());
//            System.out.println("HttpSnoopServerHandler.channelRead0 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
//            if (content.isReadable()) {
//                System.out.println("HttpSnoopServerHandler.channelRead0 CONTENT >>>>>>>>>>>>>>>>>>>>>>>");
//                System.out.println(content.toString(CharsetUtil.UTF_8));
//                System.out.println("HttpSnoopServerHandler.channelRead0 CONTENT <<<<<<<<<<<<<<<<<<<<<<<");
//            }

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;
                //System.out.println("HttpSnoopServerHandler.channelRead0 END OF CONTENT >>>>>>>>>>>>>>>>>>");
                //trailer.trailingHeaders().forEach(entryConsumer);
                //System.out.println("HttpSnoopServerHandler.channelRead0 END OF CONTENT <<<<<<<<<<<<<<<<<<");

                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1, trailer.getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
                        Unpooled.copiedBuffer("Alles goot!", CharsetUtil.UTF_8));
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
