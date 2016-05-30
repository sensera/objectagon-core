package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

/**
 * Created by christian on 2016-02-23.
 */
public class MessageValue<V> implements Message.Value {

    public static Message.Value text(Message.Field field, String value) { return new MessageValue<>(field, value);}
    public static Message.Value text(String value) { return new MessageValue<>(NamedField.text("TEXT"), value);}
    public static Message.Value number(Message.Field field, Long value) { return new MessageValue<>(field, value);}
    public static Message.Value name(Message.Field field, Name value) { return value != null ? new MessageValue<>(field, value) : empty();}
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
    public static Message.Value field(Message.Field field, Message.Field value) { return new MessageValue<>(field, value);}
    public static Message.Value empty() {return UnknownValue.create(StandardField.UNKNOWN);}

    Message.Field field;
    V value;

    public MessageValue(Message.Field field, V value) {
        this.field = field;
        this.value = value;
        validate(field, value);
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
    public Message.Field asField() {
        return (Message.Field) value;
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
                case Name: writer.write(field, (Name) asName()); break;
                case MessageName: writer.write(field, asMessageName()); break;
                case Message: writer.write(field, asMessage()); break;
                case Values: writer.write(field, asValues()); break;
                default:
                    writer.write(field, value != null ? value.toString(): "");
                    //throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
            }
        } catch (Exception e) {
            System.out.println("MessageValue.writeTo "+e.getMessage());
            //e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageValue)) return false;
        MessageValue<?> that = (MessageValue<?>) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "MessageValue{" +
                "field=" + field +
                ", value=" + value +
                '}';
    }

    private static Message.Values iterableToValues(Iterable<Message.Value> values) {
        if (values==null)
            return () -> Collections.EMPTY_LIST;
        List<Message.Value> valuesListCopy = StreamSupport.stream(values.spliterator(), false).collect(Collectors.toList());
        return () -> valuesListCopy;
    }

    private static Message.Values streamToValues(Stream<Message.Value> values) {
        List<Message.Value> valuesListCopy = values.collect(Collectors.toList());
        return () -> valuesListCopy;
    }

    private void validate(Message.Field field, V value) {
        switch (field.getFieldType()) {
            case Address: validate(field, value, Address.class); break;
            case Number: validate(field, value, Long.class); break;
            case Text: validate(field, value, String.class); break;
            case Name: validate(field, value, Name.class); break;
            case MessageName: validate(field, value, Message.MessageName.class); break;
            case Message: validate(field, value, MessageValueMessage.class); break;
            case Values: validate(field, value, Message.Values.class); break;
            default: //TODO the rest?
        }
    }

    private void validate(Message.Field field, V value, Class cls) {
        if (!cls.isInstance(value))
            throw new ClassCastException(""+value+" is not type of "+field.getName());
    }

}
