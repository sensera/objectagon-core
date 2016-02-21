package org.objectagon.core.msg.message;

import org.objectagon.core.utils.Util;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.singleton;
import static org.objectagon.core.utils.Util.printValuesToString;

/**
 * Created by christian on 2015-10-08.
 */
public class OneValueMessage extends AbstractMessage {
    private Value value;

    public static OneValueMessage oneValue(MessageName name, Value value) { return new OneValueMessage(name, value);}

    private OneValueMessage(MessageName name, Value value) {
        super(name);
        if (value==null)
            throw new NullPointerException("Value is null!");
        this.value = value;
    }

    public Value getValue(Field field) {
        if (value.getField().equals(field))
            return value;
        return UnknownValue.create(field);
    }

    @Override
    public Iterable<Value> getValues() {
        return singleton(value);
    }

    @Override
    public String toString() {
        return getName() + printValuesToString(getValues());
    }
}
