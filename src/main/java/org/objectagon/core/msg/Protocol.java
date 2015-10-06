package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Protocol {
    String getName();

    interface Send {
        void send(Address target, Message message);
    }
}
