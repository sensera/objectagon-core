package org.objectagon.core.service.event;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;

/**
 * Created by christian on 2015-11-28.
 */
public class BroadcastEventServiceProtocolImpl extends AbstractProtocol<BroadcastEventServiceProtocol.Send, Protocol.Reply> implements BroadcastEventServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(BROADCAST_EVENT_SERVICE_PROTOCOL, new SingletonFactory<>(BroadcastEventServiceProtocolImpl::new));
    }

    public BroadcastEventServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, BROADCAST_EVENT_SERVICE_PROTOCOL);
        createSend = BroadcastEventServiceProtocolSend::new;
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
