package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;

import java.util.Optional;

/**
 * Created by christian on 2016-02-26.
 */
public interface Transaction extends Identity {
    Message.Field TRANSACTION = NamedField.address("Transaction");
    Message.Field EXTENDS = NamedField.address("Extends");
    Optional<Transaction> extendsTransaction();
}
