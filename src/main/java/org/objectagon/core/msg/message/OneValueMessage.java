package org.objectagon.core.msg.message;

/**
 * Created by christian on 2015-10-08.
 */
public class OneValueMessage extends AbstractMessage {
    private Value value;

    public OneValueMessage(MessageName name, Value value) {
        super(name);
        if (this.value==null)
            throw new NullPointerException("Value is null!");
        this.value = value;
    }

    public Value getValue(Field field) {
        if (value.getField().equals(field))
            return value;
        return new UnknownValue(field);

    }
}
