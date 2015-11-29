package org.objectagon.core.msg.message;

import com.sun.xml.internal.bind.api.impl.NameConverter;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileAddressValue implements Message.Value {

    public static VolatileAddressValue address(Message.Field field, Address value) { return new VolatileAddressValue(field, value);}

    public static VolatileAddressValue address(Address value) { return new VolatileAddressValue(StandardField.ADDRESS, value);}

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
    public Message.MessageName asMessage() {return null;}
    public Name asName() {return null;}

    protected VolatileAddressValue(Message.Field field, Address value) {
        this.field = field;
        this.value = value;
    }
    public void writeTo(Message.Writer writer) {
        writer.write(value);
    }


}
