package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.storage.Identity;

/**
 * Created by christian on 2015-10-18.
 */
public interface RelationClass extends Entity<RelationClass.RelationClassIdentity, RelationClass.RelationClassData> {

    EntityName ENTITY_NAME = EntityName.create("RELATION_CLASS");

    Message.Field RELATION_CLASS_IDENTITY = NamedField.address("RELATION_CLASS_IDENTITY");
    Message.Field RELATION_NAME = NamedField.name("RELATION_NAME");
    Message.Field RELATION_TYPE = NamedField.text("RELATION_TYPE");

    Data.Type DATA_TYPE = DataType.create("RELATION_CLASS");

    interface RelationName extends Name {}

    enum RelationType  {
        INHERITANCE,
        AGGREGATE,
        ASSOCIATION,

    }
    interface RelationClassIdentity extends Identity {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
        RelationType getRelationType();
    }
    interface RelationClassData extends Data<RelationClassIdentity, StandardVersion> {
        default Type getDataType() {return DATA_TYPE;}

        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
        RelationType getRelationType();
    }
    interface RelationClassDataChange extends Data.Change<RelationClassIdentity, StandardVersion> {

    }
}
