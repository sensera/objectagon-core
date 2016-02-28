package org.objectagon.core.storage.persistence;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.DataMessageValue;
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
        public Task pushData(Data data) {
            return task(
                    MessageName.PUSH_DATA,
                    send -> send.send(MessageName.PUSH_DATA, address(data.getIdentity()), data.getVersion().asValue(), DataMessageValue.data(data)));
        }

        @Override
        public Task pushDataVersion(DataVersion dataVersion) {
            return task(
                    MessageName.PUSH_DATA_VERSION,
                    send -> send.send(MessageName.PUSH_DATA_VERSION, address(dataVersion.getIdentity()), dataVersion.getVersion().asValue(), DataMessageValue.data(dataVersion)));
        }

        @Override
        public Task getData(Identity identity, Version version) {
            return task(
                    MessageName.GET_DATA,
                    send -> send.send(MessageName.GET_DATA, address(identity), version.asValue()));
        }

        @Override
        public Task getDataVersion(Identity identity, Version version) {
            return task(
                    MessageName.GET_DATA_VERSION,
                    send -> send.send(MessageName.GET_DATA, address(identity), version.asValue()));
        }

        @Override
        public Task all(Identity identity) {
            return task(
                    MessageName.ALL,
                    send -> send.send(MessageName.ALL, address(identity)));
        }
    }
}
