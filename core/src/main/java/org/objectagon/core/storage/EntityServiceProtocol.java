package org.objectagon.core.storage;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.NamedAddress;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-17.
 */
public interface EntityServiceProtocol extends Protocol<EntityServiceProtocol.Send, Protocol.Reply> {

    ProtocolName ENTITY_SERVICE_PROTOCOL = new ProtocolNameImpl("ENTITY_SERVICE_PROTOCOL");

    Address NAMED_ENITY_SERVICE_ADDRESS = new NamedAddress(ENTITY_SERVICE_PROTOCOL);

    enum MessageName implements Message.MessageName, Task.TaskName {
        CREATE,
        GET,
        DELETE,
    }

    enum FieldName implements Message.Field {
        PERSISTENCY_SERVICE("PERSISTENCY_SERVICE", Message.FieldType.Address);

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
        Task create(Message.Value... initialValues);
        Task get(Identity identity);
        Task delete(Identity identity);
    }

}
