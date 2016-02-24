package org.objectagon.core.storage.persistence;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Version;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;
import static org.objectagon.core.msg.message.VolatileNameValue.name;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceServiceProtocolImpl extends AbstractProtocol<PersistenceServiceProtocol.Send, Protocol.Reply> implements PersistenceServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(PERSISTENCE_SERVICE_PROTOCOL, PersistenceServiceProtocolImpl::new);
    }

    public PersistenceServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new ProtocolAddressImpl(PERSISTENCE_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public PersistenceServiceProtocol.Send createSend(CreateSendParam createSend) {
        return new PersistenceServiceProtocolSend(createSend);
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }


    private class PersistenceServiceProtocolSend extends AbstractProtocolSend implements PersistenceServiceProtocol.Send {
        public PersistenceServiceProtocolSend(CreateSendParam createSend) {
            super(createSend);
        }

        @Override
        public Task create(Data data) {
            return task(
                    MessageName.CREATE,
                    send -> send.send(MessageName.CREATE, address(data.getIdentity()), data.getVersion().asValue(), MessageValue.values(data.values())));
        }

        @Override
        public Task update(Data data) {
            return task(
                    MessageName.UPDATE,
                    send -> send.send(MessageName.UPDATE, address(data.getIdentity()), data.getVersion().asValue(), MessageValue.values(data.values())));
        }

        @Override
        public Task remove(Identity identity, Version version) {
            return task(
                    MessageName.REMOVE,
                    send -> send.send(MessageName.REMOVE, address(identity), version.asValue()));
        }

        @Override
        public Task get(Identity identity, Version version) {
            return task(
                    MessageName.GET,
                    send -> send.send(MessageName.GET, address(identity), version.asValue()));
        }

        @Override
        public Task all(Identity identity) {
            return task(
                    MessageName.ALL,
                    send -> send.send(MessageName.ALL, address(identity)));
        }
    }
}
