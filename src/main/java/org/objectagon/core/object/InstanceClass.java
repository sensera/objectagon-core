package org.objectagon.core.object;

import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.stream.Stream;

/**
 * Created by christian on 2015-10-18.
 */
public interface InstanceClass extends Entity<InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData> {


    interface InstanceClassIdentity extends Identity {

    }

    interface InstanceClassData extends Data<InstanceClass.InstanceClassIdentity, StandardVersion> {
        Stream<Field.FieldName> getFieldNames();
        Stream<Relation.RelationName> getRelationNames();
        InstanceClassDataField getField(Field.FieldName fieldName);
        InstanceClassDataRelation getRelation(Relation.RelationName relationName);
    }

    interface InstanceClassDataField {
        Field.FieldName getName();
        Field.FieldType getType();
    }

    interface InstanceClassDataRelation {
        Relation.RelationName getName();
        Relation.RelationType getType();
        Relation.RelationDirection getDirection();

    }

    interface ChangeInstanceClass extends Data.Change<InstanceClass.InstanceClassIdentity,StandardVersion> {
        ChangeField setField(Field.FieldName fieldName);
        ChangeRelation setRelation(Relation.RelationName relationName);
    }

    interface ChangeField extends ChangeInstanceClass {
        ChangeField setType(Field.FieldType type);
    }

    interface ChangeRelation extends ChangeInstanceClass {
        ChangeRelation setType(Relation.RelationType type);
        ChangeRelation setDirection(Relation.RelationDirection direction);
    }
}
