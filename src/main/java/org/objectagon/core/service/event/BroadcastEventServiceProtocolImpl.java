package org.objectagon.core.service.event;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.protocol.AbstractProtocol;

import static org.objectagon.core.msg.message.SimpleMessage.simple;

/**
 * Created by christian on 2015-11-28.
 */
public class BroadcastEventServiceProtocolImpl extends AbstractProtocol<BroadcastEventServiceProtocol.Session> implements BroadcastEventServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerProtocol(BROADCAST_EVENT_SERVICE_PROTOCOL, BroadcastEventServiceProtocolImpl::new);
    }

    public BroadcastEventServiceProtocolImpl(Server.ServerId serverId) {
        super(BROADCAST_EVENT_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public BroadcastEventServiceProtocol.Session createSession(SessionOwner sessionOwner) {
        return new BroadcastEventServiceProtocolSession(nextSessionId(), sessionOwner);
    }

    private class BroadcastEventServiceProtocolSession extends AbstractProtocolSession implements BroadcastEventServiceProtocol.Session {

        public BroadcastEventServiceProtocolSession(SessionId sessionId, SessionOwner sessionOwner) {
            super(sessionId, sessionOwner);
        }

        @Override
        public void broadcast(Message.MessageName message, Iterable<Message.Value> values) {
            send(message, values);
        }

    }
}
