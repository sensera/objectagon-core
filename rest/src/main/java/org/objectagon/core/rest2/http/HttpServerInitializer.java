package org.objectagon.core.rest2.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsHandler;

import static io.netty.handler.codec.http.HttpMethod.*;

/**
 * Created by christian on 2016-02-11.
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private HttpCommunicator httpCommunicator;

    public HttpServerInitializer(HttpCommunicator httpCommunicator) {
        this.httpCommunicator = httpCommunicator;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        CorsConfig corsConfig = CorsConfig.withAnyOrigin()
                .allowedRequestHeaders("OBJECTAGON_REST_TOKEN")
                .allowedRequestMethods(PUT,POST,GET,DELETE)
                .build();
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
        p.addLast(new CorsHandler(corsConfig));
        p.addLast(new HttpProtocolSessionServerHandler(httpCommunicator));
        p.addLast(new HttpSnoopServerHandler());
    }
}
