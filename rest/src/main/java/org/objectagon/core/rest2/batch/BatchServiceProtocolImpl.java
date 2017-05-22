package org.objectagon.core.rest2.batch;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2017-04-23.
 */
public class BatchServiceProtocolImpl extends AbstractProtocol<BatchServiceProtocol.Send, Protocol.Reply> implements BatchServiceProtocol {
    public static void registerAtServer(Server server) {
        server.registerFactory(BATCH_SERVICE_PROTOCOL, new SingletonFactory<>(BatchServiceProtocolImpl::new));
    }
    public BatchServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, BATCH_SERVICE_PROTOCOL);
        createSend = BatchServiceProtocolSend::new;
    }

    private class BatchServiceProtocolSend extends AbstractProtocolSend implements BatchServiceProtocol.Send {

        public BatchServiceProtocolSend(CreateSendParam sendParam) {
            super(sendParam);
        }

        @Override
        public Task batchUpdate(Iterable<Message.Value> updateCommandField, boolean newTransaction) {
            final Task task = task(MessageName.BATCH_UPDATE, send -> {
                send.send(
                        MessageName.BATCH_UPDATE,
                        MessageValue.values(UPDATE_COMMAND_FIELD, updateCommandField),
                        MessageValue.text(NEW_TRANSACTION_FIELD,newTransaction?"true":"false"));
            });
            return task;
        }
    }
}
