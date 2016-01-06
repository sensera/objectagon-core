package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.utils.Util;

import java.util.HashMap;
import java.util.Map;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;
import static org.objectagon.core.msg.message.VolatileMessageValue.messageName;
import static org.objectagon.core.msg.message.VolatileNameValue.name;
import static org.objectagon.core.msg.message.VolatileTextValue.text;

/**
 * Created by christian on 2015-10-06.
 */
public class SimpleMessage extends AbstractMessage implements Message {

    private Map<Field,Value> values = new HashMap<>();

    public static SimpleMessage simple(MessageName name, Value... values) {
        return values.length == 0 ? new SimpleMessage(name) : new SimpleMessage(name, values);
    }

    public static SimpleMessage simple(MessageName name, Iterable<Value> values) {
        return new SimpleMessage(name, values);
    }

    private SimpleMessage(MessageName name) {
        super(name);
    }

    private SimpleMessage(MessageName name, Value... values) {
        super(name);
        for (Value value : values)
            this.values.put(value.getField(), value);
    }

    private SimpleMessage(MessageName name, Iterable<Value> values) {
        super(name);
        values.forEach(value -> this.values.put(value.getField(), value));
    }

    public Value getValue(Field field) {
        return values.get(field);
    }

    @Override
    public Iterable<Value> getValues() {return values.values();}

    public SimpleMessage setValue(Field field, Value value) {
        values.put(field, value);
        return this;
    }

    public SimpleMessage setValue(Field field, Address address) {
        return setValue(field, address(field, address));
    }

    public SimpleMessage setValue(Field field, Name name) {
        return setValue(field, name(field, name));
    }

    public SimpleMessage setValue(Field field, String text) {
        return setValue(field, text(field, text));
    }

    public SimpleMessage setValue(Field field, Message.MessageName message) {
        return setValue(field, messageName(field, message));
    }

    public SimpleMessage setValue(Value value) { values.put(value.getField(), value); return this;}

    public SimpleMessage setValues(Iterable<Value> values) { values.forEach(this::setValue); return this;}

    @Override
    public String toString() {
        return ""+getName() + Util.printValuesToString(getValues());
    }
}
