package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileMessageValue extends MessageValue<Message.MessageName>  {

    public static VolatileMessageValue messageName(Message.Field field, Message.MessageName value) { return new VolatileMessageValue(field, value);}

    public VolatileMessageValue(Message.Field field, Message.MessageName value) {
        super(field, value);
    }

    @Override
    public Message.MessageName asMessageName() {
        return value;
    }

    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }
}
