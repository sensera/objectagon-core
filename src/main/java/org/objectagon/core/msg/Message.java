package org.objectagon.core.msg;

import java.util.Collections;

/**
 * Created by christian on 2015-10-06.
 */
public interface Message {
    Iterable<Value> NO_VALUES = Collections.EMPTY_LIST;

    MessageName getName();

    Value getValue(Field field);

    Iterable<Value> getValues();

    interface MessageName extends Name {}
    interface FieldName extends Name {}

    interface Value {
        Field getField();
        String asText();
        Long asNumber();
        <A extends Address> A asAddress();
        MessageName asMessage();
        Name asName();
        Values asValues();

        void writeTo(Writer writer);
    }

    interface Field {
        FieldName getName();
        FieldType getFieldType();
        boolean sameField(Value value);
    }

    enum FieldType {
        Any,
        Text,
        Number,
        Float,
        Address,
        Blob,
        Message,
        Name,
        Password,
        Values
    }

    interface Writer {
        void write(Field field, String text);
        void write(Field field, Long number);
        void write(Field field, Message message);
        void write(Field field, Address address);
        void write(Field field, Name name);
        void write(Field field, Values values);
    }

    @FunctionalInterface
    interface Values {
        Iterable<Value> values();
    }
}
