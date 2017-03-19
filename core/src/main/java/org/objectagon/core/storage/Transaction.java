package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-02-26.
 */
public interface Transaction extends Identity {
    Message.Field TRANSACTION = NamedField.address("TRANSACTION");
    Message.Field EXTENDS = NamedField.address("EXTENDS");
    Optional<Transaction> extendsTransaction();

    static Consumer<Transaction> addAsHeader(TaskBuilder taskBuilder) {
        return (transaction -> taskBuilder.addHeader(MessageValue.address(Transaction.TRANSACTION, transaction)));
    }
}
