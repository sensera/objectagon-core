package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.service.ServiceProtocol;

/**
 * Created by christian on 2015-10-08.
 */
public class VolatileMessageValue extends MessageValue<Message.MessageName>  {

    public static VolatileMessageValue messageName(Message.Field field, Message.MessageName value) { return new VolatileMessageValue(field, value);}

    public VolatileMessageValue(Message.Field field, Message.MessageName value) {
        super(field, value);
    }

    @Override
    public Message.MessageName asMessage() {
        return value;
    }

    public void writeTo(Message.Writer writer) {
        writer.write(field, value);
    }
}
