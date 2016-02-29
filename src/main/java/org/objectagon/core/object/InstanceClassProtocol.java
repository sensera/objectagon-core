package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-11-01.
 */
public interface InstanceClassProtocol extends Protocol<InstanceClassProtocol.Send, InstanceClassProtocol.Reply> {

    ProtocolName INSTANCE_CLASS_PROTOCOL = new ProtocolNameImpl("INSTANCE_CLASS_PROTOCOL");

    enum MessageName implements Message.MessageName {
        CREATE_INSTANCE,
        ADD_FIELD,
        ADD_RELATION
    }

    enum FieldName implements Message.Field {
        FIELD("FIELD", Message.FieldType.Address),
        VALUE("VALUE", Message.FieldType.Any),
        INSTANCE_CLASS("INSTANVE_CLASS", Message.FieldType.Address),
        RELATION_CLASS("RELATION_CLASS", Message.FieldType.Address),
        VALUES("VALUES", Message.FieldType.Values),
        RELATIONS("RELATIONS", Message.FieldType.Values),
        RELATION_TYPE("RELATION_TYPE", Message.FieldType.Text),
        RELATED_CLASS("RELATED_CLASS", Message.FieldType.Address);

        private Message.FieldName name;
        private Message.FieldType fieldType;

        public Message.FieldName getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
        }
    }

    interface Send extends Protocol.Send {
        Task addField(Field.FieldName name);
        Task addRelation(Relation.RelationName name, Relation.RelationType type, InstanceClass.InstanceClassIdentity relatedClass);
        Task createInstance(Message.MessageName constructor, Message.Values constructorParams);
    }

    interface Reply extends Protocol.Reply {}
}

