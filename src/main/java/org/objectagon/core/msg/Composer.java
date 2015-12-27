package org.objectagon.core.msg;

import org.objectagon.core.task.StandardTask;

/**
 * Created by christian on 2015-10-06.
 */
public interface Composer {
    Envelope create(Message message);
    Envelope create(Message.MessageName messageName);
    Envelope create(Message.MessageName messageName, Message.Value... values);

    Builder builder(Message.MessageName messageName);

    Composer alternateReceiver(Receiver receiver);

    interface Builder {
        Builder set(Message.Field field, Message.Value value);
        Envelope create();
    }
}
