package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.EntityServiceProtocol;

/**
 * Created by christian on 2015-10-17.
 */
public class EntityServiceProtocolImpl extends AbstractProtocol<EntityServiceProtocol.Session> implements EntityServiceProtocol {

    public EntityServiceProtocolImpl(ProtocolName name, Composer composer, Transporter transporter) {
        super(name, composer, transporter);
    }

    public ClientSession createClientSession(Address target) {
        return null;
    }

    public EntityServiceProtocol.Session createSession(Address target) {
        return null;
    }

    public EntityServiceProtocol.Session createSession(Composer composer, Address target) {
        return null;
    }
}
