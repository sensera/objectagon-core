package org.objectagon.core.msg.message;

import lombok.EqualsAndHashCode;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2016-02-23.
 */
@EqualsAndHashCode
public class MessageValue<V> implements Message.Value {

    public static Message.Value text(Message.Field field, String value) { return new MessageValue<>(field, value);}
    public static Message.Value number(Message.Field field, Long value) { return new MessageValue<>(field, value);}
    public static Message.Value name(Message.Field field, Name value) { return new MessageValue<>(field, value);}
    public static Message.Value name(Name value) { return new MessageValue<>(StandardField.NAME, value);}
    public static Message.Value messageName(Message.Field field, Message.MessageName value) { return new MessageValue<>(field, value);}
    public static Message.Value address(Message.Field field, Address value) { return new MessageValue<>(field, value);}
    public static Message.Value address(Address value) { return new MessageValue<>(StandardField.ADDRESS, value);}
    public static Message.Value values(Message.Values value) { return new MessageValue<>(StandardField.VALUES, value);}
    public static Message.Value values(Iterable<Message.Value> values) { return new MessageValue<Message.Values>(StandardField.VALUES, () -> values);}

    Message.Field field;
    V value;

    public MessageValue(Message.Field field, V value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public <X> X getValue() {
        return (X) value;
    }

    @Override
    public Message.Field getField() {
        return field;
    }

    @Override
    public String asText() {
        return (String) value;
    }

    @Override
    public Long asNumber() {
        return (Long) value;
    }

    @Override
    public <A extends Address> A asAddress() {
        return (A) value;
    }

    @Override
    public Message.MessageName asMessage() {
        return (Message.MessageName) value;
    }

    @Override
    public <N extends Name> N asName() {
        return (N) value;
    }

    @Override
    public Message.Values asValues() {
        return (Message.Values) value;
    }

    @Override
    public void writeTo(Message.Writer writer) {
        switch (field.getFieldType()) {
            case Address: writer.write(field, (Address) asAddress());
            case Number: writer.write(field, asNumber());
            case Text: writer.write(field, asText());
            case Name: writer.write(field, asName());
            case Message: writer.write(field, asMessage());
            case Values: writer.write(field, asValues());
            default:
                throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
        }
    }

    @Override
    public String toString() {
        return field.getName()+" "+value;
    }


}
