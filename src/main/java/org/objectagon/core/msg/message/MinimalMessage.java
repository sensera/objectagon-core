package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-08.
 */
public class MinimalMessage extends AbstractMessage {
    public MinimalMessage(MessageName name) {
        super(name);
    }

    public Value getValue(Field field) {
        return new UnknownValue(field);
    }
}
