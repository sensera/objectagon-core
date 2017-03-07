package org.objectagon.core.rest2.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.objectagon.core.utils.AbstractStartStopController;
import org.objectagon.core.utils.FailedToStartException;
import org.objectagon.core.utils.FailedToStopException;

/**
 * Created by christian on 2017-01-21.
 */
public class HttpServerImpl extends AbstractStartStopController implements HttpService.HttpServer {

    private final int port;
    private final HttpCommunicator httpCommunicator;
    private ServerBootstrap b;
    private Channel ch;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public HttpServerImpl(HttpCommunicator httpCommunicator, int port) {
        this.httpCommunicator = httpCommunicator;
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
                    .childHandler(new HttpServerInitializer(httpCommunicator));

            ch = b.bind(port).sync().channel();
            System.out.println("HttpServerImpl.protectedStart LISTENING on "+port);
        } catch (InterruptedException e) {
            throw new FailedToStartException(e);
        }
    }

    @Override
    protected void protectedStop() throws FailedToStopException {
        try {
            bossGroup.shutdownGracefully().await();
            workerGroup.shutdownGracefully().await();
        } catch (InterruptedException e) {
            throw new FailedToStopException(e);
        }

    }
}
