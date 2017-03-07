package org.objectagon.core.rest2.http;

/**
 * Created by christian on 2016-02-11.
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpProtocolSessionServerHandler extends SimpleChannelInboundHandler<HttpObject>  {

    private HttpCommunicator httpCommunicator;

    private HttpRequest request;
    Map<String,String> params = new HashMap<>();
    HttpCommunicator.AsyncContent asyncContent;

    public HttpProtocolSessionServerHandler(HttpCommunicator httpCommunicator) {
        this.httpCommunicator = httpCommunicator;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;

            final URI uri = new URI(request.getUri());
            final String path = uri.getPath();
            final String method = request.getMethod().name();


            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            queryStringDecoder.parameters().forEach((name, values) -> params.put(name, values.get(0)));

            List<HttpCommunicator.HttpParamValues> paramValues = queryStringDecoder.parameters().keySet().stream()
                    .map(HttpCommunicator.createHttpParamValueFunction(queryStringDecoder.parameters()))
                    .collect(Collectors.toList());

            asyncContent = httpCommunicator.sendMessageWithAsyncContent(method, path, paramValues);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            this.asyncContent.pushContent(httpContent.content());

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;

                this.asyncContent
                        .completed()
                        .receiveReply(
                                replyContent -> responseAsString(channelHandlerContext, trailer, replyContent),
                                error -> responseAsString(channelHandlerContext, trailer, error.getLocalizedMessage()));
            }
        }

    }

    private void responseAsString(ChannelHandlerContext channelHandlerContext, LastHttpContent trailer, String replyContent) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, trailer.getDecoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                Unpooled.copiedBuffer(replyContent, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");

        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        channelHandlerContext.write(response);
        if (!keepAlive) {
            channelHandlerContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        asyncContent = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        this.asyncContent.exceptionCaught(cause);

        asyncContent = null;
    }
}
