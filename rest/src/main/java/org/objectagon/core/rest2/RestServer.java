package org.objectagon.core.rest2;

import org.objectagon.core.Server;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.server.ServerImpl;

/**
 * Created by christian on 2017-01-19.
 */
public class RestServer  {

    private final ServerImpl server;
    RestServerBasicConfiguration restServerBasicConfiguration;

    public RestServer(Server.ServerId serverId) {
        this.server = new ServerImpl(serverId);
        restServerBasicConfiguration = new RestServerBasicConfiguration(server);
    }

    public static void main(String[] param) throws InterruptedException {
        RestServer restServer = new RestServer(LocalServerId.local("test.server"));
        synchronized (restServer) {
            restServer.wait();
        }
    }

}
