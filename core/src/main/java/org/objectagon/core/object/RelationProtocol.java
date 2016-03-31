package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-03-07.
 */
public interface RelationProtocol extends Protocol<RelationProtocol.Send, Protocol.Reply> {

    ProtocolName RELATION_PROTOCOL = new ProtocolNameImpl("RELATION_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        GET_RELATED_INSTANCE,
        SET_RELATED_INSTANCE,
    }


    interface Send extends Protocol.Send {
        Task getRelatedInstance();
        Task setRelatedInstance(Instance.InstanceIdentity instanceIdentity);
    }


/*            ??? Too much copy paste?
    @FunctionalInterface
    interface FieldValueProtocolAction {
        Task action(FieldValueProtocol.Send send);
    }
*/

    interface ConfigRelation extends Entity.EntityConfig {
        RelationClass.RelationClassIdentity getRelationClassIdentity();
        Instance.InstanceIdentity getInstanceIdentity();
    }
}
