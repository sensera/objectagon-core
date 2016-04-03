package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-06.
 */
public interface FieldProtocol extends Protocol<FieldProtocol.Send, Protocol.Reply> {

    ProtocolName FIELD_PROTOCOL = new ProtocolNameImpl("FIELD_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        GET_NAME,
        SET_NAME,
        SET_TYPE,
        GET_TYPE,
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
        Task getName();
        Task setName(Field.FieldName name);
        Task getType();
        Task setType(Field.FieldType type);
    }

    @FunctionalInterface
    interface FieldValueProtocolAction {
        Task action(FieldValueProtocol.Send send);
    }

    interface ConfigField extends NamedConfiguration {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
    }

}
