package org.objectagon.core.service.event;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;

import static org.objectagon.core.msg.message.SimpleMessage.simple;

/**
 * Created by christian on 2015-11-28.
 */
public class BroadcastEventServiceProtocolImpl extends AbstractProtocol<BroadcastEventServiceProtocol.Send, Protocol.Reply> implements BroadcastEventServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(BROADCAST_EVENT_SERVICE_PROTOCOL, BroadcastEventServiceProtocolImpl::new);
    }

    public BroadcastEventServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new ProtocolAddressImpl(BROADCAST_EVENT_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public BroadcastEventServiceProtocol.Send createSend(CreateSendParam createSend) {
        return new BroadcastEventServiceProtocolSend(createSend);
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return new MinimalProtocolReply(createReply);
    }

    private class BroadcastEventServiceProtocolSend extends AbstractProtocolSend implements BroadcastEventServiceProtocol.Send {

        public BroadcastEventServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public void broadcast(Message.MessageName message, Iterable<Message.Value> values) {
            send(message, values);
        }

    }
}
