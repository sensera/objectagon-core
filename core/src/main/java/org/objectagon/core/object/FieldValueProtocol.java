package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-11-29.
 */
public interface FieldValueProtocol extends Protocol<FieldValueProtocol.Send, Protocol.Reply> {

    ProtocolName FIELD_VALUE_PROTOCOL = new ProtocolNameImpl("FIELD_VALUE_PROTOCOL");

    Forward createForward(CreateSendParam createSend);

    enum MessageName implements Message.MessageName, Task.TaskName {
        GET_VALUE,
        SET_VALUE,
        ADD_VALUE,
        REMOVE_VALUE,
    }

    enum FieldName implements Message.Field {
        FIELD("FIELD", Message.FieldType.Address),
        VALUE("VALUE", Message.FieldType.Any),
        INSTANCE_CLASS("INSTANVE_CLASS", Message.FieldType.Address),
        RELATION_CLASS("RELATION_CLASS", Message.FieldType.Address),
        VALUES("VALUES", Message.FieldType.Values),
        RELATIONS("RELATIONS", Message.FieldType.Values);

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
        Task getValue();
        Task setValue(Message.Value value);
    }

    interface Forward extends Protocol.Send {
        void getValue();
        void setValue(Message.Value value);
    }

    @FunctionalInterface
    interface FieldValueProtocolAction {
        Task action(FieldValueProtocol.Send send);
    }

    interface ConfigFieldValue extends Entity.EntityConfig {
        Field.FieldIdentity getFieldIdentity();
        Instance.InstanceIdentity getInstanceIdentity();
    }

}
