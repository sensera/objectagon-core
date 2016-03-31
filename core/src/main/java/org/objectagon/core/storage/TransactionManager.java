package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-03-29.
 */
public interface TransactionManager extends Receiver<Transaction> {

    Message.Field DATA_TRANSACTION = NamedField.any("DATA_TRANSACTION");

    enum Tasks implements Task.TaskName {
        LockAll,
        CommitAll,
    }

    interface TransactionData extends Data<Transaction,StandardVersion> {
        Stream<Identity> getIdentities();
    }

    interface TransactionDataChange extends Data.Change<Transaction,StandardVersion> {
        TransactionDataChange add(Identity identity);
        TransactionDataChange remove(Identity identity);
    }

}
