package org.objectagon.core.rest;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by christian on 2016-02-11.
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServerCore serverCore;
    private ProcessorLocator.Locator locator;

    public HttpServerInitializer(ServerCore serverCore, ProcessorLocator.Locator locator) {
        this.serverCore = serverCore;
        this.locator = locator;
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
        p.addLast(new HttpProtocolSessionServerHandler(serverCore, locator));
        p.addLast(new HttpSnoopServerHandler());
    }
}
