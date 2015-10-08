package org.objectagon.core.msg.message;

import org.objectagon.core.msg.Message;

/**
 * Created by christian on 2015-10-08.
 */
public class NamedField implements Message.Field {
    private FieldName name;
    private Message.FieldType fieldType;

    public Message.Name getName() {return name;}
    public Message.FieldType getFieldType() {return fieldType;}

    public NamedField(String name, Message.FieldType fieldType) {
        this.name = new FieldName(name);
        this.fieldType = fieldType;
    }
}
