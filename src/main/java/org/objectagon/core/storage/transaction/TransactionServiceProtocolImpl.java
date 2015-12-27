package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.TransactionServiceProtocol;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionServiceProtocolImpl extends AbstractProtocol<TransactionServiceProtocol.Session> implements TransactionServiceProtocol {

    public TransactionServiceProtocolImpl(ProtocolName name, Server.ServerId serverId) {
        super(name, serverId);
    }

    @Override
    public TransactionServiceProtocol.Session createSession(SessionOwner sessionOwner) {
        return new TransactionServiceProtocolSession(nextSessionId(), sessionOwner);
    }

    private class TransactionServiceProtocolSession extends AbstractProtocolSession implements TransactionServiceProtocol.Session {
        public TransactionServiceProtocolSession(SessionId sessionId, SessionOwner sessionOwner) {
            super(sessionId, sessionOwner);
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void add() {

        }

        @Override
        public void remove() {

        }

        @Override
        public void commit() {

        }

        @Override
        public void rollback() {

        }

    }

}
