package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.EntityServiceProtocol;

/**
 * Created by christian on 2015-10-17.
 */
public class EntityServiceProtocolImpl extends AbstractProtocol<EntityServiceProtocol.Session> implements EntityServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerProtocol(ENTITY_SERVICE_PROTOCOL, EntityServiceProtocolImpl::new);
    }

    public EntityServiceProtocolImpl(Server.ServerId serverId) {
        super(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL, serverId);
    }

    public ClientSession createClientSession(Address target) {
        return null;
    }

    @Override
    public EntityServiceProtocol.Session createSession(SessionOwner sessionOwner) {
        return new EntityServiceProtocolSession(nextSessionId(), sessionOwner);
    }

    private class EntityServiceProtocolSession extends AbstractProtocolSession implements EntityServiceProtocol.Session {
        public EntityServiceProtocolSession(SessionId sessionId, SessionOwner sessionOwner) {
            super(sessionId, sessionOwner);
        }
    }
}
