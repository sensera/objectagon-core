package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileTextValue extends MessageValue<String> {

    public static VolatileTextValue text(Message.Field field, String value) { return new VolatileTextValue(field, value);}

    public String asText() {
        return value;
    }

    protected VolatileTextValue(Message.Field field, String value) {
        super(field, value);
    }

    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }
}
