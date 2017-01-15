package org.objectagon.core.rest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.objectagon.core.rest.processor.*;
import org.objectagon.core.utils.AbstractStartStopController;
import org.objectagon.core.utils.FailedToStartException;

/**
 * Created by christian on 2016-02-11.
 */
public class HttpServerImpl extends AbstractStartStopController implements HttpServer {

    private final ServerCore serverCore;
    private final int port;
    ServerBootstrap b;
    Channel ch;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
    ProcessorLocator.Locator locator;

    public HttpServerImpl(ServerCore serverCore, int port, ProcessorLocator.Locator locator) {
        this.serverCore = serverCore;
        this.port = port;
        this.locator = locator;
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
                    .childHandler(new HttpServerInitializer(serverCore, locator));

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
        ServerCore serverCore = ServerCore.get();

        ProcessorLocator.Locator locator = createLocator();

        HttpServerImpl httpServer = new HttpServerImpl(serverCore, 9900, locator);
        httpServer.start();
        System.out.println("HttpServerImpl.main STARTED");
        synchronized (httpServer) {
            httpServer.wait();
        }
        System.out.println("HttpServerImpl.main ENDED");
    }

    private static ProcessorLocator.Locator createLocator() {
        ProcessorLocator processorLocator = new ProcessorLocatorImpl();
        ProcessorLocator.LocatorBuilder locatorBuilder = processorLocator.locatorBuilder();

        EntityRestProcessor.attachToLocator(locatorBuilder);
        TransactionRestProcessor.attachToLocator(locatorBuilder);
        TransactionServiceRestProcessor.attachToLocator(locatorBuilder);

        ClassRestProcessor.attachToLocator(locatorBuilder);
        MetaRestProcessor.attachToLocator(locatorBuilder);
        MethodRestProcessor.attachToLocator(locatorBuilder);
        FieldRestProcessor.attachToLocator(locatorBuilder);

        InstanceRestProcessor.attachToLocator(locatorBuilder);

        return locatorBuilder.build();
    }
}

/*
PUT http://localhost:9900/transaction/
PUT http://localhost:9900/class/


*/