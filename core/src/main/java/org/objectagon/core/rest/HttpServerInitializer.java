package org.objectagon.core.rest;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by christian on 2016-02-11.
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private Server server;
    private Address nameServiceAddress;

    public HttpServerInitializer(Server server, Address nameServiceAddress) {
        this.server = server;
        this.nameServiceAddress = nameServiceAddress;
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
        p.addLast(new HttpProtocolSessionServerHandler(server, nameServiceAddress));
        p.addLast(new HttpSnoopServerHandler());
    }
}
