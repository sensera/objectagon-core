package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.TransactionManagerProtocol;
import org.objectagon.core.task.Task;

import static org.objectagon.core.msg.message.MessageValue.address;

/**
 * Created by christian on 2016-03-29.
 */
public class TransactionManagerProtocolImpl extends AbstractProtocol<TransactionManagerProtocol.Send, TransactionManagerProtocol.Reply> implements TransactionManagerProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(TRANSACTION_MANAGER_PROTOCOL, new SingletonFactory<>(TransactionManagerProtocolImpl::new));
    }
    public TransactionManagerProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, TRANSACTION_MANAGER_PROTOCOL);
        createSend = TransactionManagerProtocolSend::new;
    }

    private class TransactionManagerProtocolSend extends AbstractProtocolSend implements TransactionManagerProtocol.Send {
        public TransactionManagerProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task addEntityTo(Identity identity) {
            return task(MessageName.ADD_ENTITY_TO, send -> send.send(MessageName.ADD_ENTITY_TO, address(identity)));
        }

        @Override
        public Task removeEntityFrom(Identity identity) {
            return task(MessageName.REMOVE_ENTITY_FROM, send -> send.send(MessageName.REMOVE_ENTITY_FROM, address(identity)));
        }

        @Override
        public Task remove() {
            return null;
        }

        @Override
        public Task commit() {
            return task(MessageName.COMMIT, send -> send.send(MessageName.COMMIT));
        }

        @Override
        public Task rollback() {
            return null;
        }

        @Override
        public Task getTargets() {
            return task(MessageName.TARGETS, send -> send.send(MessageName.TARGETS));
        }

        @Override
        public Task extend() {
            return task(MessageName.EXTEND, send -> send.send(MessageName.EXTEND));
        }
    }
}
