package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.service.ServiceProtocol;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileMessageValue implements Message.Value {

    public static VolatileMessageValue messageName(Message.Field field, Message.MessageName value) { return new VolatileMessageValue(field, value);}

    private Message.Field field;
    private Message.MessageName value;

    public Message.Field getField() {return field;}
    public String asText() {
        return null;
    }
    public Long asNumber() {
        return 0l;
    }
    public Address asAddress() {return null;}
    public Message.MessageName asMessage() {return value;}
    public Name asName() {return null;}

    private VolatileMessageValue(Message.Field field, Message.MessageName value) {
        this.field = field;
        this.value = value;
    }

    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }

}
