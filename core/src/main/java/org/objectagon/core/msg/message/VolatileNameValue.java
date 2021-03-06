package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileNameValue extends MessageValue<Name> {

    public static VolatileNameValue name(Message.Field field, Name value) { return new VolatileNameValue(field, value);}

    public static VolatileNameValue name(Name value) { return new VolatileNameValue(StandardField.NAME, value);}

    protected VolatileNameValue(Message.Field field, Name value) {
        super(field, value);
    }

    public <N extends Name> N asName() {return (N) value;}
    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }

}
