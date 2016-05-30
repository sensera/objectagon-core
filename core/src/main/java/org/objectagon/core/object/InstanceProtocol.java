package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

/**
 * Created by christian on 2015-11-01.
 */
public interface InstanceProtocol extends Protocol<InstanceProtocol.Send, Protocol.Reply> {

    ProtocolName INSTANCE_PROTOCOL = new ProtocolNameImpl("INSTANCE_PROTOCOL");

    Alter createAlter(CreateSendParam createSend);
    Internal createInternal(CreateSendParam createSendParam);

    enum MessageName implements Message.MessageName, Task.TaskName {
        GET_VALUE,
        SET_VALUE,
        ADD_VALUE,
        REMOVE_VALUE,
        GET_RELATION,
        ADD_RELATION,
        SET_RELATION,
        REMOVE_RELATION,
        DROP_RELATION,

        ADD_FIELD,
        REMOVE_FIELD,
        DROP_FIELD_VALUES, // TASK
        ADD_RELATION_CLASS,
        REMOVE_RELATION_CLASS, INVOKE_METHOD,
    }

    interface Send extends Protocol.Send {
        Task getValue(Field.FieldIdentity fieldIdentity);
        Task setValue(Field.FieldIdentity fieldIdentity, Message.Value value);
        Task getRelation(RelationClass.RelationClassIdentity relationClassIdentity);
        Task addRelation(RelationClass.RelationClassIdentity relationClassIdentity, Instance.InstanceIdentity instance);
        Task removeRelation(RelationClass.RelationClassIdentity relationClassIdentity, Instance.InstanceIdentity instance);
        Task invokeMethod(Method.MethodIdentity methodIdentity, KeyValue<Method.ParamName, Message.Value>... paramValues);
    }

    interface Alter extends Protocol.Send {
        Task addField(Field.FieldIdentity fieldIdentity);
        Task removeField(Field.FieldIdentity fieldIdentity);
        Task addRelationClass(RelationClass.RelationClassIdentity relationClassIdentity);
        Task removeRelationClass(RelationClass.RelationClassIdentity relationClassIdentity);
    }

    interface Internal extends Protocol.Send {
        Task setRelation(Relation.RelationIdentity relationIdentity);
        Task dropRelation(Relation.RelationIdentity relationIdentity);
    }

    interface ConfigInstance extends NamedConfiguration {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
    }
}

