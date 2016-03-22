package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.entity.EntityName;

import java.util.stream.Stream;

/**
 * Created by christian on 2015-10-18.
 */
public interface InstanceClass extends Entity<InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData> {

    EntityName ENTITY_NAME = EntityName.create("INSTACE_CLASS");

    Message.Field INSTANCE_CLASS_IDENTITY = NamedField.address("INSTANCE_CLASS_IDENTITY");

    interface InstanceClassIdentity extends Identity {

    }

    interface InstanceClassData extends Data<InstanceClass.InstanceClassIdentity, StandardVersion> {
        Stream<Field.FieldIdentity> getFields();
        Stream<RelationClass.RelationClassIdentity> getRelations();
    }

    interface ChangeInstanceClass extends Data.Change<InstanceClass.InstanceClassIdentity,StandardVersion> {
        ChangeInstanceClass addField(Field.FieldIdentity fieldIdentity);
        ChangeInstanceClass addRelation(RelationClass.RelationClassIdentity relationClassIdentity);
        ChangeInstanceClass removeField(Field.FieldIdentity fieldIdentity);
        ChangeInstanceClass removeRelation(RelationClass.RelationClassIdentity relationClassIdentity);
    }

}
