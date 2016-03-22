package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2016-03-06.
 */
public class MessageValueFieldUtil {
    Iterable<Message.Value> values;

    public static MessageValueFieldUtil create(Message.Values values) { return new MessageValueFieldUtil(values.values());}
    public static MessageValueFieldUtil create(Iterable<Message.Value> values) { return new MessageValueFieldUtil(values);}

    private MessageValueFieldUtil(Iterable<Message.Value> values) {
        this.values = values;
    }

    public Message.Value getValueByField(Message.Field field) {
        for (Message.Value value : values)
            if (value.getField().equals(field))
                return value;
        return UnknownValue.create(field);
    }

}
