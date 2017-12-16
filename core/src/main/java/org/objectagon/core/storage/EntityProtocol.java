package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-17.
 */
public interface EntityProtocol extends Protocol<EntityProtocol.Send, Protocol.Reply> {

    ProtocolName ENTITY_PROTOCOL = new ProtocolNameImpl("ENTITY_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        ADD_TO_TRANSACTION,
        REMOVE_FROM_TRANSACTION,
        DELETE,
        COMMIT,
        ROLLBACK,
        LOCK,
        UNLOCK,
    }

    enum DelayCauseName implements DelayCause {
        LOCK_FOR_TRANSACTION,
    }

    interface Send extends Protocol.Send {
        Task addToTransaction(Transaction transaction);
        Task removeFromTransaction(Transaction transaction);
        Task delete();
        Task commit();
        Task rollback();
        Task lock(Address lock);
        Task unlock(Address lock);
    }

}
