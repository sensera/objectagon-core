package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileAddressValue implements Message.Value {

    private Message.Field field;
    private Address value;

    public Message.Field getField() {return field;}
    public String asText() {
        return "";
    }
    public Long asNumber() {
        return 0l;
    }
    public Address asAddress() {return value;}
    public Message asMessage() {return null;}

    public VolatileAddressValue(Message.Field field, Address value) {
        this.field = field;
        this.value = value;
    }
    public void writeTo(Message.Writer writer) {
        writer.write(value);
    }


}
