package org.objectagon.core.msg;


import org.objectagon.core.msg.message.NamedField;

/**
 * Created by christian on 2015-10-06.
 */
public interface Address extends Message.ToValue {
    Message.Field TIMESTAMP = NamedField.name("timestamp");
    Message.Field ADDRESS_ID = NamedField.name("addressId");
}

