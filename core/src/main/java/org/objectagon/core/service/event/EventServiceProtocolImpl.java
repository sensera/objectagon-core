package org.objectagon.core.service.event;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.MessageValue.address;
import static org.objectagon.core.msg.message.MessageValue.name;

/**
 * Created by christian on 2015-10-13.
 */
public class EventServiceProtocolImpl extends AbstractProtocol<EventServiceProtocol.Send, EntityServiceProtocol.Reply> implements EventServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(EVENT_SERVICE_PROTOCOL, new SingletonFactory<>(EventServiceProtocolImpl::new));
    }

    public EventServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, EVENT_SERVICE_PROTOCOL);
        createSend = EventServiceProtocolSendImpl::new;
    }

    protected class EventServiceProtocolSendImpl extends AbstractProtocolSend implements EventServiceProtocol.Send {

        public EventServiceProtocolSendImpl(CreateSendParam sendParam) {
            super(sendParam);
        }

        public Task startListenTo(Address address, Name name) {
            return task(MessageName.START_LISTEN_TO, send -> send.send(MessageName.START_LISTEN_TO, address(address), name(name)));
        }

        public Task stopListenTo(Address address, Name name) {
            return task(MessageName.STOP_LISTEN_TO, send -> send.send(MessageName.STOP_LISTEN_TO, address(address), name(name)));
        }

        @Override
        public void broadcast(Name name, Message.MessageName message, Message.Value... values) {
            send(MessageName.BROADCAST,
                    MessageValue.name(name),
                    MessageValue.message(message, values)
            );
        }
    }

}
