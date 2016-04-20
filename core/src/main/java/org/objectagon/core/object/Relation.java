package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-10-18.
 */
public interface Relation extends Entity<Relation.RelationIdentity, Relation.RelationData> {

    EntityName ENTITY_NAME = EntityName.create("RELATION");

    Message.Field RELATIONS = NamedField.values("RELATIONS");

    Data.Type DATA_TYPE = DataType.create("RELATION");

    interface RelationIdentity extends Identity {
        RelationClass.RelationClassIdentity getRelationClassIdentity();
        Instance.InstanceIdentity getInstanceIdentity();
        //TODO NEEDS another instance to connecto to
    }

    interface RelationData extends Data<RelationIdentity, StandardVersion> {
        default Type getDataType() {return DATA_TYPE;}
        RelationClass.RelationClassIdentity getRelationClassIdentity();
        Instance.InstanceIdentity getInstanceIdentity();
        //TODO NEEDS another instance to connecto to
    }

    interface RelationDataChange extends Data.Change<RelationIdentity, StandardVersion> {
        RelationDataChange setRelatedInstance(Instance.InstanceIdentity instanceIdentity);
    }
}
