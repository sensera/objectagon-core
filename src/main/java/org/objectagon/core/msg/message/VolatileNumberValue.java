package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileNumberValue extends MessageValue<Long> {

    public static VolatileNumberValue number(Message.Field field, Long value) { return new VolatileNumberValue(field, value);}

    public Message.Field getField() {return field;}
    public String asText() {return ""+value;}
    public Long asNumber() {return value;}

    protected VolatileNumberValue(Message.Field field, Long value) {
        super(field, value);
    }

    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }

}
