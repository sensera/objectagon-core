package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.service.ServiceProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public abstract class AbstractProtocol<U extends Protocol.Session> implements Protocol<U> {
    private ProtocolName name;
    protected Composer composer;
    protected Transporter transporter;

    public ProtocolName getName() {return name;}

    public AbstractProtocol(ProtocolName name, Composer composer, Transporter transporter) {
        this.name = name;
        this.composer = composer;
        this.transporter = transporter;
    }

    protected abstract class AbstractProtocolSession implements Protocol.Session {
        final protected Address target;
        final protected Composer composer;

        public AbstractProtocolSession(Composer composer, Address target) {
            this.composer = composer;
            this.target = target;
        }

    }

}
