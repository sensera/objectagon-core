package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christian on 2015-10-06.
 */
public class SimpleMessage extends BasicMessage implements Message {

    private Map<Field,Value> values = new HashMap<Field, Value>();

    public SimpleMessage(MessageName name) {
        super(name);
    }

    public Value getValue(Field field) {
        return values.get(field);
    }

    public void setValue(Field field, Value value) {
        values.put(field, value);
    }
}
