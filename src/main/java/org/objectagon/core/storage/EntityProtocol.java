package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;

/**
 * Created by christian on 2015-10-17.
 */
public interface EntityProtocol extends Protocol<EntityProtocol.Session> {

    ProtocolName ENTITY_PROTOCOL = new ProtocolNameImpl("ENTITY_PROTOCOL");

    ClientSession createClientSession(Address target);

    enum MessageName implements Message.MessageName {
        GET_VALUE,
        SET_VALUE,
        ADD_TO_LIST,
        REMOVE_FROM_LIST,
        DELETE,
        COMMIT,
        ROLLBACK,
    }

    enum FieldName implements Message.Field {
        ADDRESS("ADDRESS", Message.FieldType.Address),
        NAME("NAME", Message.FieldType.Name),
        MESSAGE("MESSAGE", Message.FieldType.Message),

        ERROR_DESCRIPTION("ERROR_SEVERITY", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text),
        VERSION("VERSION", Message.FieldType.Number);

        private Message.FieldName name;
        private Message.FieldType fieldType;

        public Message.FieldName getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
        }
    }

    interface Session extends Protocol.Session {
        void replyWithError(ErrorKind errorKind);
    }

    interface ClientSession extends Protocol.Session {
        void start();
        void stop();
        void add();
        void remove();
        void commit();
        void rollback();
    }

    enum ErrorKind {
    }
}
