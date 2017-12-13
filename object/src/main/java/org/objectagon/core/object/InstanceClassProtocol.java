package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by christian on 2015-11-01.
 */
public interface InstanceClassProtocol extends Protocol<InstanceClassProtocol.Send, Protocol.Reply> {

    ProtocolName INSTANCE_CLASS_PROTOCOL = new ProtocolNameImpl("INSTANCE_CLASS_PROTOCOL");
    Message.Field ALIAS_NAME = NamedField.name("aliasName");
    Message.Field FIELDS = NamedField.values("fields");
    Message.Field RELATIONS = NamedField.values("relations");
    Message.Field METHOD_FIELD_MAPPINGS = NamedField.values("methodFieldMappings");
    Message.Field METHOD_DEFAULT_MAPPINGS = NamedField.values("methodDefaultMappings");
    Message.Field KEY_VALUES = NamedField.values("keyValues");

    Internal createInternal(CreateSendParam createSendParam);

    enum MessageName implements Message.MessageName, Task.TaskName {
        CREATE_INSTANCE,
        ADD_FIELD,
        GET_FIELDS,
        GET_RELATIONS,
        GET_NAME,
        SET_NAME,
        ADD_RELATION,
        ADD_INSTANCE_ALIAS, GET_INSTANCE_BY_ALIAS, REMOVE_INSTANCE_ALIAS, ADD_METHOD, REMOVE_METHOD, INVOKE_METHOD, SET_RELATION
    }

    interface Send extends Protocol.Send {
        Task addField();
        Task addRelation(RelationClass.RelationType type, InstanceClass.InstanceClassIdentity relatedClass);
        Task createInstance();
        Task getName();
        Task getFields();
        Task setName(InstanceClass.InstanceClassName instanceClassName);
        Task addInstanceAlias(Instance.InstanceIdentity instanceIdentity, Name aliasName);
        Task removeInstanceAlias(Name aliasName);
        Task getInstanceByAlias(Name aliasName);
        Task addMethod(Method.MethodIdentity methodIdentity, Stream<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings, Stream<KeyValue<Method.ParamName, Message.Value>> defaultValues);
        Task removeMethod(Method.MethodIdentity methodIdentity);
        Task invokeMethod(Method.MethodIdentity methodIdentity, Instance.InstanceIdentity instanceIdentity, List<KeyValue<Method.ParamName, Message.Value>> params);
    }

    interface Internal extends Protocol.Send {
        Task setRelation(RelationClass.RelationClassIdentity relationClassIdentity);
    }
}

