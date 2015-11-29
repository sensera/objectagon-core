package org.objectagon.core.storage.transaction;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.TransactionServiceProtocol;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionServiceProtocolImpl extends AbstractProtocol<TransactionServiceProtocol.Session> implements TransactionServiceProtocol {

    public TransactionServiceProtocolImpl(ProtocolName name) {
        super(name);
    }

    @Override
    public TransactionServiceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return null;
    }

    private class TransactionServiceProtocolSession extends AbstractProtocolSession implements TransactionServiceProtocol.Session {
        public TransactionServiceProtocolSession(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
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
