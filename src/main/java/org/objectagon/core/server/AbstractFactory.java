package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;

/**
 * Created by christian on 2015-11-22.
 */
public abstract class AbstractFactory<R extends Receiver> implements Server.Factory<R> {

    public AbstractFactory() {
        super();
    }

}
