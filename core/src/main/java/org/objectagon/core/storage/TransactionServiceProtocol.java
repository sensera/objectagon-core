package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-13.
 */
public interface TransactionServiceProtocol extends Protocol<TransactionServiceProtocol.Send, Protocol.Reply> {

    ProtocolName TRANSACTION_SERVICE_PROTOCOL = new ProtocolNameImpl("TRANSACTION_SERVICE_PROTOCOL");

    Internal createInternal(CreateSendParam createSend);

    enum MessageName implements Message.MessageName, Task.TaskName {
        CREATE, EXTEND,
    }

    interface Send extends Protocol.Send {
        Task create();
    }

    interface Internal extends Protocol.Send {
        Task extend(Transaction transaction);
    }

}
