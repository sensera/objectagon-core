package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileNameValue implements Message.Value {

    private Message.Field field;
    private Name value;

    public Message.Field getField() {return field;}
    public String asText() {
        return "";
    }
    public Long asNumber() {
        return 0l;
    }
    public Address asAddress() {return null;}
    public Message asMessage() {return null;}
    public Name asName() {return value;}

    public VolatileNameValue(Message.Field field, Name value) {
        this.field = field;
        this.value = value;
    }
    public void writeTo(Message.Writer writer) {
        writer.write(value);
    }


}
