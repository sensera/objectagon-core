package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class AbstractMessage implements Message {

    private MessageName name;

    public MessageName getName() {return name;}

    public AbstractMessage(MessageName name) {
        this.name = name;
    }
}
