package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.*;

/**
 * Created by christian on 2015-10-06.
 */
public interface StandardProtocol extends Protocol<StandardProtocol.StandardSession> {

    ProtocolName STANDARD_PROTOCOL = new ProtocolNameImpl("STANDARD_PROTOCOL");

    enum MessageName implements Message.MessageName {
        ERROR_MESSAGE,
        OK_MESSAGE,

    }

    enum FieldName implements Message.Field {
        ERROR_DESCRIPTION("ERROR_DESCRIPTION", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text),
        PARAM("PARAM", Message.FieldType.Any);

        private Message.FieldName name;
        private Message.FieldType fieldType;

        public Message.FieldName getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
        }

    }

    interface StandardSession extends Protocol.Session {
        void replyWithError(String description, ErrorKind errorKind);
        void replyWithError(ErrorKind errorKind);
        void replyOk();
        void replyWithParam(Message.Value param);
    }

    class OkMessage extends MinimalMessage {
        public OkMessage() {
            super(StandardProtocol.MessageName.OK_MESSAGE);
        }
    }

    class OkParam extends AbstractMessage{
        Value param;
        public OkParam(Value param) {
            super(StandardProtocol.MessageName.OK_MESSAGE);
            this.param = param;
        }
        public Value getValue(Field field) {
            if (field.equals(StandardProtocol.FieldName.PARAM))
                return param;
            return new UnknownValue(field);
        }

    }

    class ErrorMessage extends AbstractMessage {
        private String description;
        private String kind;

        public ErrorMessage(String description, String kind) {
            super(StandardProtocol.MessageName.ERROR_MESSAGE);
            this.description = description;
            this.kind = kind;
        }

        public Value getValue(Field field) {
            if (field.equals(StandardProtocol.FieldName.ERROR_DESCRIPTION))
                return new VolatileTextValue(field, description);
            else if (field.equals(StandardProtocol.FieldName.ERROR_KIND))
                return new VolatileTextValue(field, kind);
            return new UnknownValue(field);
        }
    }

    enum ErrorKind {
        UnknownMessage;
    }

}
