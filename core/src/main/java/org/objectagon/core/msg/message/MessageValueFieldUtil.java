package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by christian on 2016-03-06.
 */
public class MessageValueFieldUtil {
    Iterable<Message.Value> values;

    public static MessageValueFieldUtil create(Message.Values values) { return new MessageValueFieldUtil(values.values());}
    public static MessageValueFieldUtil create(Message.Value[] values) { return new MessageValueFieldUtil(Arrays.asList(values));}
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

    public Optional<Message.Value> getValueByFieldOption(Message.Field field) {
        for (Message.Value value : values)
            if (value.getField().equals(field))
                return Optional.ofNullable(value);
        return Optional.empty();
    }

    public Stream<Message.Value> stream() {
        return StreamSupport.stream(values.spliterator(), false);
    }

}
