package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileTextValue implements Message.Value {

    public static VolatileTextValue text(Message.Field field, String value) { return new VolatileTextValue(field, value);}

    private Message.Field field;
    private String value;

    public Message.Field getField() {return field;}
    public String asText() {
        return value;
    }
    public Long asNumber() {
        return Long.parseLong(value);
    }
    public <A extends Address> A  asAddress() {return null;}
    public Message.MessageName asMessage() {return null;}
    public Name asName() {return null;}

    protected VolatileTextValue(Message.Field field, String value) {
        this.field = field;
        this.value = value;
    }

    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }

    @Override
    public Message.Values asValues() {return null;}


}
