package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-11-01.
 */
public interface InstanceClassProtocol extends Protocol<InstanceClassProtocol.Send, Protocol.Reply> {

    ProtocolName INSTANCE_CLASS_PROTOCOL = new ProtocolNameImpl("INSTANCE_CLASS_PROTOCOL");
    Message.Field ALIAS_NAME = NamedField.name("aliasName");
    Message.Field FIELDS = NamedField.values("fields");
    Message.Field RELATIONS = NamedField.values("relations");

    Internal createInternal(CreateSendParam createSendParam);

    enum MessageName implements Message.MessageName, Task.TaskName {
        CREATE_INSTANCE,
        ADD_FIELD,
        GET_FIELDS,
        GET_RELATIONS,
        GET_NAME,
        SET_NAME,
        ADD_RELATION,
        ADD_INSTANCE_ALIAS, GET_INSTANCE_BY_ALIAS, REMOVE_INSTANCE_ALIAS, SET_RELATION
    }

    interface Send extends Protocol.Send {
        Task addField();
        Task addRelation(RelationClass.RelationType type, InstanceClass.InstanceClassIdentity relatedClass);
        Task createInstance();
        Task getName();
        Task setName(InstanceClass.InstanceClassName instanceClassName);
        Task addInstanceAlias(Instance.InstanceIdentity instanceIdentity, Name aliasName);
        Task removeInstanceAlias(Name aliasName);
        Task getInstanceByAlias(Name aliasName);
    }

    interface Internal extends Protocol.Send {
        Task setRelation(RelationClass.RelationClassIdentity relationClassIdentity);
    }
}

