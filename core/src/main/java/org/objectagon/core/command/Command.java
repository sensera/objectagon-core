package org.objectagon.core.command;

import org.objectagon.core.Server;

/**
 * Created by christian on 2017-01-15.
 */
public interface Command {
    void register(ServicePackage servicePackage);

    interface ServicePackage {
        void register(Server server);
    }
}
