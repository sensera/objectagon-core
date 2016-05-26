package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by christian on 2015-10-18.
 */
public interface InstanceClass extends Entity<InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData> {

    EntityName ENTITY_NAME = EntityName.create("INSTACE_CLASS");

    Message.Field INSTANCE_CLASS_IDENTITY = NamedField.address("instanceClassId");
    Message.Field INSTANCE_CLASS_NAME = NamedField.name("InstanceClassName");

    Message.Field FIELDS = NamedField.values("fields");
    Message.Field RELATIONS = NamedField.values("relations");

    Data.Type DATA_TYPE = DataType.create("INSTANCE_CLASS");

    enum NameTask implements Task.TaskName {
        NAME_SEARCH
    }

    interface InstanceClassIdentity extends Identity {}
    interface InstanceClassName extends Name {}

    interface InstanceClassData extends Data<InstanceClass.InstanceClassIdentity, StandardVersion> {
        default Type getDataType() {return DATA_TYPE;}
        Stream<Field.FieldIdentity> getFields();
        Stream<RelationClass.RelationClassIdentity> getRelations();
        InstanceClassName getName();
        Optional<Instance.InstanceIdentity> getInstanceByAliasName(Name alias);
    }

    interface ChangeInstanceClass extends Data.Change<InstanceClass.InstanceClassIdentity,StandardVersion> {
        ChangeInstanceClass addField(Field.FieldIdentity fieldIdentity);
        ChangeInstanceClass addRelation(RelationClass.RelationClassIdentity relationClassIdentity);
        ChangeInstanceClass removeField(Field.FieldIdentity fieldIdentity);
        ChangeInstanceClass removeRelation(RelationClass.RelationClassIdentity relationClassIdentity);
        ChangeInstanceClass setName(InstanceClassName instanceClassName);
        ChangeInstanceClass addInstanceAlias(Instance.InstanceIdentity instanceIdentity, Name alias);
        ChangeInstanceClass removeInstanceAlias(Name instanceAlias);
    }

}
