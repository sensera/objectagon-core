package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Message {
    MessageName getName();

    Value getValue(Field field);

    interface MessageName extends Name {}
    interface FieldName extends Name {}

    interface Value {
        Field getField();
        String asText();
        Long asNumber();
        Address asAddress();
        Message asMessage();
        Name asName();

        void writeTo(Writer writer);
    }

    interface Field {
        FieldName getName();
        FieldType getFieldType();
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
        ArrayOfTexts,
        ArrayOfNumbers,
        ArrayOfFloats,
        ArrayOfAddresses
    }

    interface Writer {
        void write(String text);
        void write(Long number);
        void write(Message message);
        void write(Address address);
        void write(Name name);
    }
}
