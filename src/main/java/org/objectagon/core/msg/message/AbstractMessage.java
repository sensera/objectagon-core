package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

import java.util.Optional;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class AbstractMessage implements Message {

    private MessageName name;

    public MessageName getName() {return name;}


    public AbstractMessage(MessageName name) {
        this.name = name;
    }

    protected static Optional<Value> lookupValueInParamsByField(Iterable<Value> values, Field field) {
        if (values==null)
            return Optional.empty();
        for (Value value : values)
            if (value.getField().equals(field))
                return Optional.of(value);
        return Optional.empty();
    }
}
