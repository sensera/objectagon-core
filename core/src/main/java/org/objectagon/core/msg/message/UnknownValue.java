package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by christian on 2015-10-08.
 */
public class UnknownValue implements Message.Value {

    private Message.Field field;

    public static UnknownValue create(Message.Field field) { return new UnknownValue(field);}
    public static UnknownValue text() { return new UnknownValue(NamedField.text("Unknown text"));}
    public static UnknownValue name() { return new UnknownValue(NamedField.name("Unknown name"));}

    private UnknownValue(Message.Field field) {
        this.field = field;
    }

    public Message.Field getField() {
        return field;
    }

    public String asText() {
        return "";
    }

    public Long asNumber() {
        return new Long(0);
    }

    @Override
    public <A extends Address> A  asAddress() {return null;}

    @Override
    public Message.MessageName asMessageName() {return null;}

    @Override
    public MessageValueMessage asMessage() {return null;}

    @Override
    public <N extends Name> N asName() {return null;}

    @Override
    public Message.Field asField() {return null;}

    @Override
    public Message.Values asValues() {return null;}

    @Override
    public <N extends Name, V extends Message.Value> java.util.Map<N, V> asMap() {return new HashMap<>();}

    public void writeTo(Message.Writer writer) {}

    @Override
    public <V> V getValue() {
        return null;
    }

    @Override
    public <V> Optional<V> getOptionalValue() {
        return Optional.empty();
    }
}
