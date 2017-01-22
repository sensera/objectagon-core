package org.objectagon.core.rest2.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

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
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
        p.addLast(new HttpProtocolSessionServerHandler(httpCommunicator));
        p.addLast(new HttpSnoopServerHandler());
    }
}
