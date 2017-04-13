package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-03-29.
 */
public interface TransactionManagerProtocol extends Protocol<TransactionManagerProtocol.Send, Protocol.Reply> {

    ProtocolName TRANSACTION_MANAGER_PROTOCOL = new ProtocolNameImpl("TRANSACTION_MANAGER_PROTOCOL");
    Name TRANSACTION_MANAGER_CONFIG = StandardName.name("TRANSACTION_MANAGER_CONFIG");

    enum MessageName implements Message.MessageName, Task.TaskName {
        ADD_ENTITY_TO,
        REMOVE_ENTITY_FROM,
        REMOVE,
        COMMIT,
        TARGETS,
        EXTEND,
        ROLLBACK
    }

    interface Send extends Protocol.Send {
        Task addEntityTo(Identity identity);
        Task removeEntityFrom(Identity identity);
        Task remove();
        Task commit();
        Task rollback();
        Task getTargets();
        Task extend();
    }

    interface TransactionManagerConfig extends NamedConfiguration {
        TransactionManager.TransactionData getTransactionData(Transaction transaction);
        Optional<Transaction> extendsTransaction();
    }

}
