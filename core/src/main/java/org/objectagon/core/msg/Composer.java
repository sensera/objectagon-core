package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Composer {
    Envelope create(Message message);
    Envelope create(Message.MessageName messageName);
    Envelope create(Message.MessageName messageName, Message.Value... values);

    Builder builder(Message.MessageName messageName);

    Composer alternateReceiver(Receiver receiver);

    Address getSenderAddress();

    interface Builder {
        Builder set(Message.Field field, Message.Value value);
        Envelope create();
    }

    @FunctionalInterface
    interface ResolveTarget {
        Address getAddress();
    }

    @FunctionalInterface
    interface CreateComposer {
        Composer create(Sender sender);
    }
}
