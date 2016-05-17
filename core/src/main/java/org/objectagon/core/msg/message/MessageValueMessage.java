package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;
import org.objectagon.core.utils.Util;

import java.util.List;
import java.util.Objects;

/**
 * Created by christian on 2016-03-06.
 */
public class MessageValueMessage {
    private final Message.MessageName messageName;
    private final List<Message.Value> values;

    public MessageValueMessage(Message.MessageName messageName, Iterable<Message.Value> values) {
        this.messageName = messageName;
        this.values = Util.iterableToList(values);
    }

    public Message.MessageName getMessageName() {
        return messageName;
    }

    public Iterable<Message.Value> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageValueMessage)) return false;
        MessageValueMessage that = (MessageValueMessage) o;
        return Objects.equals(getMessageName(), that.getMessageName()) &&
                Objects.equals(getValues(), that.getValues());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageName(), getValues());
    }

    @Override
    public String toString() {
        return "MessageValueMessage{" +
                "messageName=" + messageName +
                ", values=" + values +
                '}';
    }
}
