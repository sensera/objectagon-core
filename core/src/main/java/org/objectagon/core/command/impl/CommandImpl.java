package org.objectagon.core.command.impl;

import org.objectagon.core.Server;
import org.objectagon.core.command.Command;

/**
 * Created by christian on 2017-01-19.
 */
public class CommandImpl implements Command {
    Server server;

    public CommandImpl(Server server) {
        this.server = server;
    }

    @Override
    public ServiceCommand attacheTo(ServiceName serviceName) {
        return null;
    }

    @Override
    public ServiceCreator getServiceCreator() {
        return null;
    }
}
