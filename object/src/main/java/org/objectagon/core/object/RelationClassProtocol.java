package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-07.
 */
public interface RelationClassProtocol extends Protocol<RelationClassProtocol.Send, Protocol.Reply> {

    ProtocolName RELATION_CLASS_PROTOCOL = new ProtocolNameImpl("RELATION_CLASS_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        SET_NAME,
        GET_NAME,
        CREATE_RELATION,
        DESTROY_RELATION
    }

    interface Send extends Protocol.Send {
        Task getName();
        Task setName(RelationClass.RelationName relationName);
        Task createRelation(Instance.InstanceIdentity instanceIdentityFrom, Instance.InstanceIdentity instanceIdentityTo);
        Task destroyRelation(Instance.InstanceIdentity instanceIdentityFrom, Relation.RelationIdentity relationIdentity);
    }

    interface ConfigRelationClass extends NamedConfiguration {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity(RelationClass.RelationDirection relationDirection);
        RelationClass.RelationType getRelationType();
    }
}
