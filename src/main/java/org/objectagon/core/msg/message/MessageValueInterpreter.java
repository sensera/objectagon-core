package org.objectagon.core.msg.message;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-02-26.
 */
public class MessageValueInterpreter {

    Map<Message.Field,Consumer<Message.Value>> consumerFields = new HashMap<>();

    private Optional<Consumer<Message.Value>> getConsumer(Message.Field field) {
        return Optional.ofNullable(consumerFields.get(field));
    }

    public MessageValueInterpreter add(Message.Field field, Consumer<Message.Value> consumer) {
        consumerFields.put(field, consumer);
        return this;
    }

    public void interpret(Message.Values values) {
        values.values().forEach(this::checkValue);
    }

    private void checkValue(Message.Value value) {
        getConsumer(value.getField())
                .orElseThrow(() -> new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY))
                .accept(value);
    }

}
