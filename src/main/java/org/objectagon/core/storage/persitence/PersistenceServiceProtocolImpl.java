package org.objectagon.core.storage.persitence;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Transporter;
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

    public PersistenceServiceProtocolImpl() {
        super(PERSISTENCE_SERVICE_PROTOCOL);
    }

    @Override
    public PersistenceServiceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new PersistenceServiceProtocolSession(nextSessionId(), composer, transporter, sessionOwner);
    }

    private class PersistenceServiceProtocolSession extends AbstractProtocolSession implements PersistenceServiceProtocol.Session {
        public PersistenceServiceProtocolSession(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
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
