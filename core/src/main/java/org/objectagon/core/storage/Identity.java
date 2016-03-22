package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;

/**
 * Created by christian on 2015-10-15.
 */
public interface Identity extends Address {
    Message.Field IDENTITY = NamedField.address("Identity");
}
