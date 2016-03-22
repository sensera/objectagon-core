package org.objectagon.core.service.name;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.VolatileNameValue.name;

/**
 * Created by christian on 2015-10-13.
 */
public class NameServiceProtocolImpl extends AbstractProtocol<NameServiceProtocol.Send, NameServiceProtocol.Reply> implements NameServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(NAME_SERVICE_PROTOCOL, new SingletonFactory<>(NameServiceProtocolImpl::new));
    }

    public NameServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
        return new ProtocolAddressImpl(NAME_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public NameServiceProtocol.Send createSend(CreateSendParam createSend) {
        return new NameServiceProtocolSendImpl(createSend);
    }

    @Override
    public Locator createLocator(CreateSendParam createSend) {
        return new LocatorImpl(createSend);
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }

    protected class NameServiceProtocolSendImpl extends AbstractProtocolSend implements NameServiceProtocol.Send {

        public NameServiceProtocolSendImpl(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task registerName(Address address, Name name) {
            return task(
                    NameServiceProtocol.MessageName.REGISTER_NAME,
                    send -> send.send(MessageName.REGISTER_NAME,
                            VolatileAddressValue.address(StandardField.ADDRESS, address),
                            name(StandardField.NAME, name)));
        }

        @Override
        public Task unregisterName(Name name) {
            return task(
                    NameServiceProtocol.MessageName.UNREGISTER_NAME,
                    send -> send.send(MessageName.UNREGISTER_NAME,
                            name(StandardField.NAME, name)));
        }

        @Override
        public Task lookupAddressByName(Name name) {
            return task(
                    NameServiceProtocol.MessageName.LOOKUP_ADDRESS_BY_NAME,
                    send -> send.send(MessageName.LOOKUP_ADDRESS_BY_NAME, name(StandardField.NAME, name)));
        }

    }

    protected class LocatorImpl extends AbstractProtocolSend implements Locator {

        public LocatorImpl(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public void forward(Message.MessageName messageName, Iterable<Message.Value> values) {
            send(MessageName.FORWARD, MessageValue.message(messageName, values));
            terminate();
        }
    }


}
