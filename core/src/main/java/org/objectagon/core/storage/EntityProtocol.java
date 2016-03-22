package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;

/**
 * Created by christian on 2015-10-17.
 */
public interface EntityProtocol extends Protocol<EntityProtocol.Send, Protocol.Reply> {

    ProtocolName ENTITY_PROTOCOL = new ProtocolNameImpl("ENTITY_PROTOCOL");

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
        VERSION("VERSION", Message.FieldType.Number);

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

    interface Send extends Protocol.Send {
        void start();
        void stop();
        void add();
        void remove();
        void commit();
        void rollback();
    }

}
