package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileNumberValue implements Message.Value {

    public static VolatileNumberValue number(Message.Field field, Long value) { return new VolatileNumberValue(field, value);}

    private Message.Field field;
    private Long value;

    public Message.Field getField() {return field;}
    public String asText() {
        return ""+value;
    }
    public Long asNumber() {
        return value;
    }
    public <A extends Address> A  asAddress() {return null;}
    public Message.MessageName asMessage() {return null;}
    public Name asName() {return null;}

    protected VolatileNumberValue(Message.Field field, Long value) {
        this.field = field;
        this.value = value;
    }

    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }
    @Override
    public Message.Values asValues() {return null;}

}
