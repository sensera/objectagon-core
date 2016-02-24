package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;

/**
 * Created by christian on 2015-10-15.
 */
public interface Version {
    Message.Field VERSION = NamedField.number("Version");

    Message.Value asValue();

    @FunctionalInterface
    interface Create<V extends Version> {
        V create(Message.Value value);
    }
}
