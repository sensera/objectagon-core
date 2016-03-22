package org.objectagon.core.service.event;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.Server;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.EntityServiceProtocol;

import static java.util.Arrays.asList;

/**
 * Created by christian on 2015-10-13.
 */
public class EventServiceProtocolImpl extends AbstractProtocol<EventServiceProtocol.Send, EntityServiceProtocol.Reply> implements EventServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(EVENT_SERVICE_PROTOCOL, new SingletonFactory<>(EventServiceProtocolImpl::new));
    }

    public EventServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
        return new ProtocolAddressImpl(EVENT_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public EventServiceProtocol.Send createSend(CreateSendParam createSend) {
        return new EventServiceProtocolSendImpl(createSend);
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }

    protected class EventServiceProtocolSendImpl extends AbstractProtocolSend implements EventServiceProtocol.Send {

        public EventServiceProtocolSendImpl(CreateSendParam sendParam) {
            super(sendParam);
        }

        public void startListenTo(Address address, Name name) {
            transport(composer.create(
                    SimpleMessage.simple(MessageName.START_LISTEN_TO)
                            .setValue(StandardField.ADDRESS, address)
                            .setValue(StandardField.NAME, name)));
        }

        public void stopListenTo(Address address, Name name) {
            transport(composer.create(
                    SimpleMessage.simple(MessageName.STOP_LISTEN_TO)
                            .setValue(StandardField.ADDRESS, address)
                            .setValue(StandardField.NAME, name)));
        }

        @Override
        public void broadcast(Name name, Message.MessageName message, Message.Value... values) {
            transport(composer.create(
                    SimpleMessage.simple(MessageName.BROADCAST)
                            .setValue(StandardField.NAME, name)
                            .setValue(StandardField.MESSAGE, message, asList(values))));
        }
    }

}
