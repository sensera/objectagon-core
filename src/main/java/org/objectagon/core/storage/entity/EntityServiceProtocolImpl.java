package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.EntityServiceProtocol;

/**
 * Created by christian on 2015-10-17.
 */
public class EntityServiceProtocolImpl extends AbstractProtocol<EntityServiceProtocol.Session> implements EntityServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerProtocol(ENTITY_SERVICE_PROTOCOL, EntityServiceProtocolImpl::new);
    }

    public EntityServiceProtocolImpl() {
        super(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL);
    }

    public ClientSession createClientSession(Address target) {
        return null;
    }

    @Override
    public EntityServiceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new EntityServiceProtocolSession(nextSessionId(), composer, transporter, sessionOwner);
    }

    private class EntityServiceProtocolSession extends AbstractProtocolSession implements EntityServiceProtocol.Session {
        public EntityServiceProtocolSession(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
        }
    }
}
