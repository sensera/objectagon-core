package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-11-01.
 */
public interface InstanceProtocol extends Protocol<InstanceProtocol.Send, InstanceProtocol.Reply> {

    ProtocolName INSTANCE_PROTOCOL = new ProtocolNameImpl("INSTANCE_PROTOCOL");

    Alter createAlter(CreateSendParam createSend);

    enum MessageName implements Message.MessageName, Task.TaskName {
        GET_VALUE,
        SET_VALUE,
        ADD_VALUE,
        REMOVE_VALUE,
        GET_RELATION,
        ADD_RELATION,
        REMOVE_RELATION,

        ADD_FIELD,
        REMOVE_FIELD,
        DROP_FIELD_VALUES, // TASK
        ADD_RELATION_CLASS,
        REMOVE_RELATION_CLASS,
    }

    interface Send extends Protocol.Send {
        Task getValue(Field.FieldName name);
        Task setValue(Field.FieldName name, Message.Value value);
        Task getRelation(RelationClass.RelationName name);
        Task addRelation(RelationClass.RelationName name, Instance.InstanceIdentity instance);
        Task removeRelation(RelationClass.RelationName name, Instance.InstanceIdentity instance);
    }

    interface Alter extends Protocol.Send {
        Task addField(Field.FieldIdentity fieldIdentity);
        Task removeField(Field.FieldIdentity fieldIdentity);
        Task addRelationClass(RelationClass.RelationClassIdentity relationClassIdentity);
        Task removeRelationClass(RelationClass.RelationClassIdentity relationClassIdentity);
    }

    interface Reply extends Protocol.Reply {}

    interface ConfigInstance extends SetInitialValues {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
    }
}

