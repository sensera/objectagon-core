package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Composer {
    Envelope create(Message message);
    Envelope create(Message.MessageName messageName);

    Builder builder(Message message);

    interface Builder {
        Builder set(Message.Field field, Message.Value value);
        Envelope create();
    }
}
