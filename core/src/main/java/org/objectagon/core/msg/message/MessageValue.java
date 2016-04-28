package org.objectagon.core.msg.message;

import lombok.EqualsAndHashCode;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

/**
 * Created by christian on 2016-02-23.
 */
@EqualsAndHashCode
public class MessageValue<V> implements Message.Value {

    public static Message.Value text(Message.Field field, String value) { return new MessageValue<>(field, value);}
    public static Message.Value text(String value) { return new MessageValue<>(NamedField.text("TEXT"), value);}
    public static Message.Value number(Message.Field field, Long value) { return new MessageValue<>(field, value);}
    public static Message.Value name(Message.Field field, Name value) { return new MessageValue<>(field, value);}
    public static Message.Value name(Name value) { return new MessageValue<>(StandardField.NAME, value);}
    public static Message.Value messageName(Message.Field field, Message.MessageName value) { return new MessageValue<>(field, value);}
    public static Message.Value messageName(Message.MessageName messageName) { return new MessageValue<>(StandardField.MESSAGE_NAME, messageName);}
    public static Message.Value message(Message.MessageName messageName, Iterable<Message.Value> values) { return new MessageValue<>(StandardField.MESSAGE, new MessageValueMessage(messageName, values));}
    public static Message.Value message(Message.MessageName messageName, Message.Value... values) { return new MessageValue<>(StandardField.MESSAGE, new MessageValueMessage(messageName, asList(values)));}
    public static Message.Value message(Message.Field field, Message.MessageName messageName, Iterable<Message.Value> values) {
        return new MessageValue<>(field, new MessageValueMessage(messageName, values));
    }
    public static Message.Value address(Message.Field field, Address value) { return new MessageValue<>(field, value);}
    public static Message.Value address(Address value) { return new MessageValue<>(StandardField.ADDRESS, value);}
    public static Message.Value values(Message.Values value) { return new MessageValue<>(StandardField.VALUES, value);}
    public static Message.Value values(Stream<Message.Value> values) { return new MessageValue<Message.Values>(StandardField.VALUES, streamToValues(values));}
    public static Message.Value values(Iterable<Message.Value> values) { return new MessageValue<Message.Values>(StandardField.VALUES, iterableToValues(values));}
    public static Message.Value values(Message.Value... values) { return new MessageValue<Message.Values>(StandardField.VALUES, () -> asList(values));}
    public static Message.Value values(Message.Field field, Message.Value... values) { return new MessageValue<Message.Values>(field, () -> asList(values));}
    public static Message.Value values(Message.Field field, Iterable<Message.Value> values) { return new MessageValue<Message.Values>(field, iterableToValues(values));}
    public static Message.Value empty() {return UnknownValue.create(StandardField.NAME);}

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
    public <X> Optional<X> getOptionalValue() {
        return Optional.of( (X) value);
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
    public Message.MessageName asMessageName() {
        return (Message.MessageName) value;
    }

    @Override
    public MessageValueMessage asMessage() {
        return (MessageValueMessage) value;
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
        //TODO move this func to field
        try {
            switch (field.getFieldType()) {
                case Address: writer.write(field, (Address) asAddress()); break;
                case Number: writer.write(field, asNumber()); break;
                case Text: writer.write(field, asText()); break;
                //case Name: writer.write( (Name) field, asName()); break; //TODO solve this
                case MessageName: writer.write(field, asMessageName()); break;
                case Message: writer.write(field, asMessage()); break;
                case Values: writer.write(field, asValues()); break;
                default:
                    writer.write(field, value != null ? value.toString(): "");
                    //throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
            }
        } catch (Exception e) {
            System.out.println("MessageValue.writeTo "+e.getMessage());
        }
    }

    @Override
    public String toString() {
        return field.getName()+" "+value;
    }

    private static Message.Values iterableToValues(Iterable<Message.Value> values) {
        List<Message.Value> valuesListCopy = StreamSupport.stream(values.spliterator(), false).collect(Collectors.toList());
        return () -> valuesListCopy;
    }

    private static Message.Values streamToValues(Stream<Message.Value> values) {
        List<Message.Value> valuesListCopy = values.collect(Collectors.toList());
        return () -> valuesListCopy;
    }
}
