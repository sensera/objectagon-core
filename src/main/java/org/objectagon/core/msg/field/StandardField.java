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

        ERROR_DESCRIPTION("ERROR_DESCRIPTION", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text);

        private Message.FieldName name;
        private Message.FieldType fieldType;

        public Message.FieldName getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        StandardField(String name, Message.FieldType fieldType) {
                this.name = new FieldNameImpl(name);
                this.fieldType = fieldType;
        }
}
