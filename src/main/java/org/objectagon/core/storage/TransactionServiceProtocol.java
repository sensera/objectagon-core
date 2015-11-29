package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-13.
 */
public interface TransactionServiceProtocol extends Protocol<TransactionServiceProtocol.Session> {

    ProtocolName TRANSACTION_SERVICE_PROTOCOL = new ProtocolNameImpl("TRANSACTION_SERVICE_PROTOCOL");

    NamedAddress TRANSACTION_SERVICE_ADDRESS = new NamedAddress(TRANSACTION_SERVICE_PROTOCOL);

    enum MessageName implements Message.MessageName {
        START,
        END,
        ADD,
        REMOVE,
        COMMIT,
        ROLLBACK
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
        void start();
        void stop();
        void add();
        void remove();
        void commit();
        void rollback();
    }

    enum ErrorKind implements StandardProtocol.ErrorKind {
    }
}
