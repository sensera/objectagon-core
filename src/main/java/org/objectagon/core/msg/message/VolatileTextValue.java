package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileTextValue implements Message.Value {

    private Message.Field field;
    private String value;

    public Message.Field getField() {return field;}
    public String asText() {
        return value;
    }
    public Long asNumber() {
        return Long.parseLong(value);
    }
    public Address asAddress() {return null;}
    public Message asMessage() {return null;}
    public Name asName() {return null;}

    public VolatileTextValue(Message.Field field, String value) {
        this.field = field;
        this.value = value;
    }

    public void writeTo(Message.Writer writer) {
        writer.write(value);
    }

}
