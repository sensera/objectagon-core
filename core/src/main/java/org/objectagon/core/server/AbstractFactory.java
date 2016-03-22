package org.objectagon.core.server;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.Server;

/**
 * Created by christian on 2015-11-22.
 */
public abstract class AbstractFactory<R extends Receiver> implements Server.Factory<R> {

    public AbstractFactory() {
        super();
    }

}
