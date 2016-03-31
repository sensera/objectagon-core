package org.objectagon.core.storage.persistence;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.DataMessageValue;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceServiceProtocolImpl extends AbstractProtocol<PersistenceServiceProtocol.Send, Protocol.Reply> implements PersistenceServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(PERSISTENCE_SERVICE_PROTOCOL, new SingletonFactory<>(PersistenceServiceProtocolImpl::new));
    }

    public PersistenceServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void initialize(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
        super.initialize(serverId, timestamp, id, initializer);
    }


    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
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
                    send -> send.send(MessageName.PUSH_DATA, VolatileAddressValue.address(data.getIdentity()), data.getVersion().asValue(), DataMessageValue.data(data)));
        }

        @Override
        public Task pushDataAndVersion(Data data, DataVersion dataVersion) {
            return task(
                    MessageName.PUSH_DATA_AND_VERSION,
                    send -> send.send(MessageName.PUSH_DATA_AND_VERSION, MessageValue. address(data.getIdentity()), data.getVersion().asValue(), DataMessageValue.data(data), DataMessageValue.dataVersion(dataVersion)));
        }

        @Override
        public Task pushDataVersion(DataVersion dataVersion) {
            return task(
                    MessageName.PUSH_DATA_VERSION,
                    send -> send.send(MessageName.PUSH_DATA_VERSION, VolatileAddressValue.address(dataVersion.getIdentity()), dataVersion.getVersion().asValue(), DataMessageValue.dataVersion(dataVersion)));
        }

        @Override
        public Task getData(Identity identity, Version version) {
            return task(
                    MessageName.GET_DATA,
                    send -> send.send(MessageName.GET_DATA, VolatileAddressValue.address(identity), version.asValue()));
        }

        @Override
        public Task getDataVersion(Identity identity, Version version) {
            return task(
                    MessageName.GET_DATA_VERSION,
                    send -> send.send(MessageName.GET_DATA, VolatileAddressValue.address(identity), version.asValue()));
        }

        @Override
        public Task getLatestDataVersion(Identity identity) {
            return task(
                    MessageName.GET_DATA_VERSION,
                    send -> send.send(MessageName.GET_DATA, VolatileAddressValue.address(identity)));
        }

        @Override
        public Task all(Identity identity) {
            return task(
                    MessageName.ALL,
                    send -> send.send(MessageName.ALL, VolatileAddressValue.address(identity)));
        }

        @Override
        public Task pushTransactionData(TransactionManager.TransactionData transactionData) {
            return task(
                    MessageName.PUSH_TRANSACTION_DATA,
                    send -> send.send(MessageName.PUSH_TRANSACTION_DATA, MessageValue. address(transactionData.getIdentity()), transactionData.getVersion().asValue(), DataMessageValue.dataTransaction(transactionData)));
        }
    }
}
