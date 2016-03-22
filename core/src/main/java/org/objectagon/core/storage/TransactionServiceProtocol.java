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

    enum MessageName implements Message.MessageName {
        START,
        END,
        ADD,
        REMOVE,
        COMMIT,
        ROLLBACK
    }

    interface Send extends Protocol.Send {
        Task start();
        Task stop();
        Task add();
        Task remove();
        Task commit();
        Task rollback();
    }


}
