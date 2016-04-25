package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionServiceProtocol;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.MessageValue.address;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionServiceProtocolImpl extends AbstractProtocol<TransactionServiceProtocol.Send, TransactionServiceProtocol.Reply> implements TransactionServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(TRANSACTION_SERVICE_PROTOCOL, new SingletonFactory<>(TransactionServiceProtocolImpl::new));
    }

    public TransactionServiceProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, TRANSACTION_SERVICE_PROTOCOL);
        createSend = TransactionServiceProtocolSend::new;
    }

    @Override
    public Internal createInternal(CreateSendParam createSend) {
        return new TransactionServiceProtocolInternal(createSend);
    }

    private class TransactionServiceProtocolSend extends AbstractProtocolSend implements TransactionServiceProtocol.Send {
        public TransactionServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task create() {
            return task(MessageName.CREATE, send -> send.send(MessageName.CREATE));
        }
    }

    private class TransactionServiceProtocolInternal extends AbstractProtocolSend implements TransactionServiceProtocol.Internal {
        public TransactionServiceProtocolInternal(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task extend(Transaction transaction) {
            return task(MessageName.EXTEND, send -> send.send(MessageName.EXTEND, address(Transaction.EXTENDS, transaction)));
        }
    }
}
