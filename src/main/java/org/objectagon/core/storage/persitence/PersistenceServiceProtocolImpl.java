package org.objectagon.core.storage.persitence;

import org.objectagon.core.Server;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceServiceProtocolImpl extends AbstractProtocol<PersistenceServiceProtocol.Session> implements PersistenceServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerProtocol(PERSISTENCE_SERVICE_PROTOCOL, PersistenceServiceProtocolImpl::new);
    }

    public PersistenceServiceProtocolImpl(Server.ServerId serverId) {
        super(PERSISTENCE_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public PersistenceServiceProtocol.Session createSession(SessionOwner sessionOwner) {
        return new PersistenceServiceProtocolSession(nextSessionId(), sessionOwner);
    }

    private class PersistenceServiceProtocolSession extends AbstractProtocolSession implements PersistenceServiceProtocol.Session {
        public PersistenceServiceProtocolSession(SessionId sessionId, SessionOwner sessionOwner) {
            super(sessionId, sessionOwner);
        }

        @Override
        public void create(Data data) {

        }

        @Override
        public void update(Data data) {

        }

        @Override
        public void remove(Identity identity, Version version) {

        }

        @Override
        public void get(Identity identity, Version version) {

        }

        @Override
        public void all(Identity identity) {

        }
    }
}
