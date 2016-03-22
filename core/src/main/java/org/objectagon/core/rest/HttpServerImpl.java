package org.objectagon.core.rest;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.service.name.NameServiceProtocolImpl;
import org.objectagon.core.utils.AbstractStartStopController;
import org.objectagon.core.utils.FailedToStartException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.objectagon.core.server.ServerImpl;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;

/**
 * Created by christian on 2016-02-11.
 */
public class HttpServerImpl extends AbstractStartStopController implements HttpServer {

    private final Server server;
    private final Address nameServiceAddress;
    private final int port;
    ServerBootstrap b;
    Channel ch;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public HttpServerImpl(Server server, Address nameServiceAddress, int port) {
        this.server = server;
        this.nameServiceAddress = nameServiceAddress;
        this.port = port;
    }

    @Override
    protected void protectedStart() throws FailedToStartException {
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpServerInitializer(server, nameServiceAddress));

            ch = b.bind(port).sync().channel();
        } catch (InterruptedException e) {
            throw new FailedToStartException(e);
        }
    }

    @Override
    protected void protectedStop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] params) throws FailedToStartException, InterruptedException {
        Server server = new ServerImpl(LocalServerId.local("Home"));
        NameServiceProtocolImpl.registerAtServer(server);
        NameServiceProtocolImpl nameServiceProtocol = server.createReceiver(NameServiceProtocol.NAME_SERVICE_PROTOCOL, null);
        NameServiceImpl.registerAtServer(server);
        NameServiceImpl nameService = server.createReceiver(NameServiceImpl.NAME_SERVICE_ADDRESS, null);

        HttpServerImpl httpServer = new HttpServerImpl(server, nameService.getAddress(), 9900);
        httpServer.start();
        System.out.println("HttpServerImpl.main STARTED");
        synchronized (httpServer) {
            httpServer.wait();
        }
        System.out.println("HttpServerImpl.main ENDED");
    }
}
