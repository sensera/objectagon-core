package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-06.
 */
public interface Message {
    Name getName();

    Value getValue(Field field);

    interface Name {}

    interface Value {
        Field getField();
        String asText();
        Long asNumber();
        void writeTo(Writer writer);
    }

    interface Field {
        Name getName();
        FieldType getFieldType();
    }

    enum FieldType {
        Text,
        Number,
        Float,
        Address,
        Blob,
        ArrayOfTexts,
        ArrayOfNumbers,
        ArrayOfFloats,
        ArrayOfAddresses
    }

    interface Writer {
        void write(String text);
        void write(Long number);
    }
}
