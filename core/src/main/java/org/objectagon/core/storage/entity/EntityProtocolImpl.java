package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.storage.EntityProtocol;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-30.
 */
public class EntityProtocolImpl extends AbstractProtocol<EntityProtocol.Send, Protocol.Reply> implements EntityProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(ENTITY_PROTOCOL, new SingletonFactory<>(EntityServiceProtocolImpl::new));
    }

    public EntityProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, ENTITY_PROTOCOL);
        createSend = EntityProtocolSend::new;
    }

    private class EntityProtocolSend extends AbstractProtocolSend implements EntityProtocol.Send {
        public EntityProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task addToTransaction(Transaction transaction) {
            return task(MessageName.ADD_TO_TRANSACTION, send -> send.send(MessageName.ADD_TO_TRANSACTION, MessageValue.address(transaction)));
        }

        @Override
        public Task removeFromTransaction(Transaction transaction) {
            return task(MessageName.REMOVE_FROM_TRANSACTION, send -> send.send(MessageName.REMOVE_FROM_TRANSACTION, MessageValue.address(transaction)));
        }

        @Override
        public Task delete() {
            return task(MessageName.DELETE, send -> send.send(MessageName.DELETE));
        }

        @Override
        public Task commit() {
            return task(MessageName.COMMIT, send -> send.send(MessageName.COMMIT));
        }

        @Override
        public Task rollback() {
            return task(MessageName.ROLLBACK, send -> send.send(MessageName.ROLLBACK));
        }

        @Override
        public Task lock(Address lock) {
            return task(MessageName.LOCK, send -> send.send(MessageName.LOCK, MessageValue.address(lock)));
        }

        @Override
        public Task unlock(Address lock) {
            return task(MessageName.UNLOCK, send -> send.send(MessageName.UNLOCK, MessageValue.address(lock)));
        }
    }
}
