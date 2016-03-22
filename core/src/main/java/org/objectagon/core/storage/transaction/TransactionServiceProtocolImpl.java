package org.objectagon.core.storage.transaction;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.TransactionServiceProtocol;
import org.objectagon.core.Server;
import org.objectagon.core.msg.protocol.ProtocolAddressImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionServiceProtocolImpl extends AbstractProtocol<TransactionServiceProtocol.Send, TransactionServiceProtocol.Reply> implements TransactionServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(TRANSACTION_SERVICE_PROTOCOL, new SingletonFactory<>(TransactionServiceProtocolImpl::new));
    }

    public TransactionServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<ProtocolAddress> initializer) {
        return new ProtocolAddressImpl(TRANSACTION_SERVICE_PROTOCOL, serverId);
    }

    @Override
    public TransactionServiceProtocol.Send createSend(CreateSendParam createSend) {
        return new TransactionServiceProtocolSend(createSend);
    }

    @Override
    public Reply createReply(CreateReplyParam createReply) {
        return new AbstractProtocolReply(createReply) {};
    }

    private class TransactionServiceProtocolSend extends AbstractProtocolSend implements TransactionServiceProtocol.Send {
        public TransactionServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task start() {
            return null;
        }

        @Override
        public Task stop() {

            return null;
        }

        @Override
        public Task add() {

            return null;
        }

        @Override
        public Task remove() {

            return null;
        }

        @Override
        public Task commit() {

            return null;
        }

        @Override
        public Task rollback() {

            return null;
        }

    }

}
