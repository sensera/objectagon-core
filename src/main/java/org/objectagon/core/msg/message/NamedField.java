package org.objectagon.core.msg.message;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;

import java.util.Objects;

/**
 * Created by christian on 2015-10-08.
 */
public class NamedField implements Message.Field {

    public static NamedField text(String name) { return new NamedField(name, Message.FieldType.Text);}
    public static NamedField number(String name) { return new NamedField(name, Message.FieldType.Number);}
    public static NamedField address(String name) { return new NamedField(name, Message.FieldType.Address);}
    public static NamedField any(String name) { return new NamedField(name, Message.FieldType.Any);}
    public static NamedField blob(String name) { return new NamedField(name, Message.FieldType.Blob);}
    public static NamedField message(String name) { return new NamedField(name, Message.FieldType.Message);}
    public static NamedField name(String name) { return new NamedField(name, Message.FieldType.Name);}
    public static NamedField password(String name) { return new NamedField(name, Message.FieldType.Password);}
    public static NamedField values(String name) { return new NamedField(name, Message.FieldType.Values);}

    private FieldNameImpl name;
    private Message.FieldType fieldType;

    public Message.FieldName getName() {return name;}
    public Message.FieldType getFieldType() {return fieldType;}

    @Override
    public boolean sameField(Message.Value value) {
        return equals(value.getField());
    }

    protected NamedField(String name, Message.FieldType fieldType) {
        this.name = new FieldNameImpl(name);
        this.fieldType = fieldType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedField)) return false;
        NamedField that = (NamedField) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
