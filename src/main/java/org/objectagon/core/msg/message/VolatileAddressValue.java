package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileAddressValue extends MessageValue<Address> implements Message.Value {

    public static VolatileAddressValue address(Message.Field field, Address value) { return new VolatileAddressValue(field, value);}

    public static VolatileAddressValue address(Address value) { return new VolatileAddressValue(StandardField.ADDRESS, value);}


    protected VolatileAddressValue(Message.Field field, Address value) {
        super(field, value);
    }
    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }

}
