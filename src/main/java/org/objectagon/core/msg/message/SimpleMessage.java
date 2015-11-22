package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christian on 2015-10-06.
 */
public class SimpleMessage extends AbstractMessage implements Message {

    private Map<Field,Value> values = new HashMap<>();

    public SimpleMessage(MessageName name) {
        super(name);
    }

    public SimpleMessage(MessageName name, Value... values) {
        super(name);
        for (Value value : values)
            this.values.put(value.getField(), value);
    }

    public Value getValue(Field field) {
        return values.get(field);
    }

    public SimpleMessage setValue(Field field, Value value) {
        values.put(field, value);
        return this;
    }

    public SimpleMessage setValue(Field field, Address address) {
        return setValue(field, new VolatileAddressValue(field, address));
    }

    public SimpleMessage setValue(Field field, Name name) {
        return setValue(field, new VolatileNameValue(field, name));
    }

    public SimpleMessage setValue(Field field, String text) {
        return setValue(field, new VolatileTextValue(field, text));
    }

    public SimpleMessage setValue(Field field, Message message) {
        return setValue(field, new VolatileMessageValue(field, message));
    }
}
