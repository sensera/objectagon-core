package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;

/**
 * Created by christian on 2016-02-26.
 */
public interface Transaction extends Address {
    Message.Field TRANSACTION = NamedField.address("Transaction");
}
