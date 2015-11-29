package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-18.
 */
public interface PersistenceServiceProtocol extends Protocol<PersistenceServiceProtocol.Session> {

    ProtocolName PERSISTENCE_SERVICE_PROTOCOL = new ProtocolNameImpl("PERSISTENCE_SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName {
        CREATE,
        UPDATE,
        REMOVE,
        GET,
        ALL,
    }

    enum FieldName implements Message.Field {
        ADDRESS("ADDRESS", Message.FieldType.Address),
        NAME("NAME", Message.FieldType.Name),
        MESSAGE("MESSAGE", Message.FieldType.Message),

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

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
        }
    }

    interface Session extends Protocol.Session {
        void create(Data data);
        void update(Data data);
        void remove(Identity identity, Version version);
        void get(Identity identity, Version version);
        void all(Identity identity);
    }

    enum ErrorKind implements StandardProtocol.ErrorKind {
    }
}
