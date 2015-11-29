package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;

/**
 * Created by christian on 2015-11-29.
 */
public interface FieldValueProtocol extends Protocol<FieldValueProtocol.Session> {

    ProtocolName FIELD_VALUE_PROTOCOL = new ProtocolNameImpl("FIELD_VALUE_PROTOCOL");

    enum MessageName implements Message.MessageName {
        GET_VALUE,
        SET_VALUE,
        ADD_VALUE,
        REMOVE_VALUE,
        GET_REATION,
        ADD_RELATION,
        REMOVE_RELATION,
    }

    enum FieldName implements Message.Field {
        ERROR_DESCRIPTION("ERROR_SEVERITY", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text),

        FIELD("FIELD", Message.FieldType.Address),
        VALUE("VALUE", Message.FieldType.Any),
        INSTANCE_CLASS("INSTANVE_CLASS", Message.FieldType.Address),
        RELATION_CLASS("RELATION_CLASS", Message.FieldType.Address),
        VALUES("VALUES", Message.FieldType.ListOfAddresses),
        RELATIONS("RELATIONS", Message.FieldType.ListOfAddresses);

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
        void getValue();
        void setValue(Message.Value value);
    }


}
