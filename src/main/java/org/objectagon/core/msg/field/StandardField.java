package org.objectagon.core.msg.field;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.FieldNameImpl;

/**
 * Created by christian on 2015-10-17.
 */
public enum StandardField implements Message.Field {

    ADDRESS("ADDRESS", Message.FieldType.Address),
    NAME("NAME", Message.FieldType.Name),
    MESSAGE("MESSAGE", Message.FieldType.Message),
    VALUES("VALUES", Message.FieldType.Values),
    SESSION_ID("SESSION_ID", Message.FieldType.Number),

    ERROR_DESCRIPTION("ERROR_SEVERITY", Message.FieldType.Text),
    ERROR_KIND("ERROR_KIND", Message.FieldType.Text);

    private Message.FieldName name;
    private Message.FieldType fieldType;

    public Message.FieldName getName() {return name;}
    public Message.FieldType getFieldType() {return fieldType;}

    @Override
    public boolean sameField(Message.Value value) {
        return equals(value.getField());
    }

    StandardField(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
    }
}
