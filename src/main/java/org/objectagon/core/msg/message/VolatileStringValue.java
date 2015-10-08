package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileStringValue implements Message.Value {

    private Message.Field field;
    private String value;

    public VolatileStringValue(Message.Field field, String value) {
        this.field = field;
        this.value = value;
    }

    public Message.Field getField() {
        return field;
    }

    public String asText() {
        return value;
    }

    public Long asNumber() {
        return Long.parseLong(value);
    }

    public void writeTo(Message.Writer writer) {
        writer.write(value);
    }
}
