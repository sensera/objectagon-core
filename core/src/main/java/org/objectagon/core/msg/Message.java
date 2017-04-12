package org.objectagon.core.msg;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueMessage;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.message.UnknownValue;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2015-10-06.
 */
public interface Message {
    Message.Field MESSAGE_HEADER_TRACE_ACTIVE = NamedField.text("trace");

    Iterable<Value> NO_VALUES = Collections.EMPTY_LIST;

    MessageName getName();

    Value getValue(Field field);

    Iterable<Value> getValues();

    interface MessageName extends Name {}
    interface FieldName extends Name {}

    interface Value {
        Field getField();
        <V> V getValue();
        <V> Optional<V> getOptionalValue();
        String asText();
        Long asNumber();
        <A extends Address> A asAddress();
        MessageName asMessageName();
        MessageValueMessage asMessage();
        <N extends Name> N asName();
        Field asField();
        Values asValues();
        java.util.Map<? extends Name, ? extends Value> asMap();

        default boolean isUnknown() { return this instanceof UnknownValue; }

        void writeTo(Writer writer);
    }

    interface Field {
        FieldName getName();
        FieldType getFieldType();

        default void verifyField(Message.Value value) {
            if (!this.equals(value.getField()))
                throw new SevereError(ErrorClass.FIELD, ErrorKind.WRONG_FIELD, value, MessageValue.name(getName()));
        }

        default boolean sameField(Message.Value value) {
            return equals(value.getField());
        }

        default Value createValueFromString(String value) {
            switch (getFieldType()) {
                case Text: return MessageValue.text(this, value);
                case Number: return MessageValue.number(this, Long.parseLong(value));
                case Any: return MessageValue.text(this, value);
                default:
                    throw new SevereError(ErrorClass.FIELD, ErrorKind.TEXT_CONVERT_NOT_SUPPORTED_BY_FIELD);
            }
        }
    }

    enum FieldType {
        Any,
        Text,
        Number,
        Float,
        Address,
        Blob,
        Message,
        MessageName,
        Name,
        Password,
        Values,
        Map;
    }

    interface Writer {
        void write(Field field, String text);
        void write(Field field, Long number);
        void write(Field field, MessageValueMessage message);
        void write(Field field, Message.MessageName messageName);
        void write(Field field, Address address);
        void write(Field field, Name name);
        void write(Field field, Values values);
        void write(Field field, Map<? extends Name, ? extends Value> map);
    }

    @FunctionalInterface
    interface Values {
        Iterable<Value> values();
    }

    interface Builder extends BuilderItem {
        Value build();
    }

    interface BuilderItem {
        BuilderValue create(String name);
        BuilderValue create(Field field);
        BuilderItem values(String name);
        BuilderItem values();
        BuilderItem values(Field field);
    }

    interface BuilderValue {
        void set(String value);
        void set(Long value);
        void set(Value value);
        void set(Message value);
        void set(MessageName value);
        void set(Name value);
        void set(Address value);
    }

    interface ToValue {
        void toValue(BuilderItem builderItem);
    }

    interface Transform<Target> {
        Value transform(Target target);
        Target transform(Value value);
    }
}
