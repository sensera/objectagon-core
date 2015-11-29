package org.objectagon.core.service.event;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.AbstractProtocol;

import static org.objectagon.core.msg.message.SimpleMessage.simple;

/**
 * Created by christian on 2015-11-28.
 */
public class BroadcastEventServiceProtocolImpl extends AbstractProtocol<BroadcastEventServiceProtocol.Session> implements BroadcastEventServiceProtocol {

    public BroadcastEventServiceProtocolImpl(ProtocolName name) {
        super(name);
    }

    @Override
    public BroadcastEventServiceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new BroadcastEventServiceProtocolSession(nextSessionId(), composer, transporter, sessionOwner);
    }

    private class BroadcastEventServiceProtocolSession extends AbstractProtocolSession implements BroadcastEventServiceProtocol.Session {

        public BroadcastEventServiceProtocolSession(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
        }

        @Override
        public void broadcast(Message.MessageName message, Iterable<Message.Value> values) {
            send(message, values);
        }

    }
}
