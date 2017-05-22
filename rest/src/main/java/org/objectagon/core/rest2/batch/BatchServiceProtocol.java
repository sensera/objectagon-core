package org.objectagon.core.rest2.batch;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2017-04-14.
 */
public interface BatchServiceProtocol extends Protocol<BatchServiceProtocol.Send, Protocol.Reply> {

    ProtocolName BATCH_SERVICE_PROTOCOL = new ProtocolNameImpl("BATCH_SERVICE_PROTOCOL");

    Message.Field UPDATE_COMMAND_FIELD  = NamedField.values("updateCommandField");
    Message.Field NEW_TRANSACTION_FIELD  = NamedField.text("newTransaction");
    Message.Field NAMES_FIELD  = NamedField.values("names");

    enum MessageName implements Message.MessageName, Task.TaskName {
        BATCH_UPDATE
    }

    interface Send extends Protocol.Send {
        Task batchUpdate(Iterable<Message.Value> updateCommandField, boolean newTransaction);
    }

}
