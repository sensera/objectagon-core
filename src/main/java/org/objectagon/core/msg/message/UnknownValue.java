package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-08.
 */
public class UnknownValue implements Message.Value {

    private Message.Field field;

    public UnknownValue(Message.Field field) {
        this.field = field;
    }

    public Message.Field getField() {
        return field;
    }

    public String asText() {
        return "";
    }

    public Long asNumber() {
        return new Long(0);
    }

    public void writeTo(Message.Writer writer) {}
}
