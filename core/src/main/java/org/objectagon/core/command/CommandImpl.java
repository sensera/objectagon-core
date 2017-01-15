package org.objectagon.core.command;

import org.objectagon.core.Server;

/**
 * Created by christian on 2017-01-15.
 */
public class CommandImpl implements Command {

    Server server;

    public CommandImpl(Server server) {
        this.server = server;
    }

    @Override
    public void register(ServicePackage servicePackage) {
        servicePackage.register(server);
    }
}
