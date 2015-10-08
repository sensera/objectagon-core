package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.*;

/**
 * Created by christian on 2015-10-06.
 */
public interface StandardProtocol extends Protocol<StandardProtocol.StandardSession> {

    enum MessageName implements Message.Name {
        ERROR_MESSAGE,

    }

    enum FieldName implements Message.Field {
        ERROR_DESCRIPTION("ERROR_DESCRIPTION", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text);

        private Message.Name name;
        private Message.FieldType fieldType;

        public Message.Name getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new org.objectagon.core.msg.message.FieldName(name);
            this.fieldType = fieldType;
        }

    }

    interface StandardSession extends Protocol.Session {
        void sendErrorTo(String description, ErrorKind errorKind);
    }

    class ErrorMessage extends BasicMessage {
        private String description;
        private String kind;

        public ErrorMessage(String description, String kind) {
            super(MessageName.ERROR_MESSAGE);
            this.description = description;
            this.kind = kind;
        }

        public Value getValue(Field field) {
            if (field.equals(FieldName.ERROR_DESCRIPTION))
                return new VolatileStringValue(field, description);
            else if (field.equals(FieldName.ERROR_KIND))
                return new VolatileStringValue(field, kind);
            return new UnknownValue(field);
        }
    }

    enum ErrorKind {
        UnknownMessage;
    }

}
