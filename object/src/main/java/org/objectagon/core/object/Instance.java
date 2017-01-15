package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2015-10-18.
 */
public interface Instance extends Entity<Instance.InstanceIdentity, Instance.InstanceData> {

    EntityName ENTITY_NAME = EntityName.create("INSTANCE");

    Message.Field INSTANCE_IDENTITY = NamedField.address("instanceId");

    Data.Type DATA_TYPE = DataType.create("INSTANCE");

    enum TaskName implements Message.MessageName, Task.TaskName {
        SET_VALUE,
        GET_VALUE,
    }

    interface InstanceIdentity extends Identity {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
    }

    interface InstanceData extends Data<InstanceIdentity,StandardVersion> {
        default Type getDataType() {return DATA_TYPE;}

        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();

        Stream<FieldValue.FieldValueIdentity> getFieldValues();
        Stream<Relation.RelationIdentity> getRelations();
    }

    interface InstanceDataChange extends Data.Change<InstanceIdentity,StandardVersion> {
        InstanceDataChange addField(FieldValue.FieldValueIdentity fieldValueIdentity);
        InstanceDataChange removeField(FieldValue.FieldValueIdentity fieldValueIdentity);
        InstanceDataChange addRelation(Relation.RelationIdentity relationIdentity);
        InstanceDataChange removeRelation(Relation.RelationIdentity relationIdentity);
    }

}
