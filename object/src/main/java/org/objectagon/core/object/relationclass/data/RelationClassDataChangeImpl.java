package org.objectagon.core.object.relationclass.data;

import org.objectagon.core.object.RelationClass;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassDataChangeImpl implements RelationClass.RelationClassDataChange {

    private RelationClass.RelationClassData relationClassData;
    private Optional<RelationClass.RelationName> relationName;

    public RelationClassDataChangeImpl(RelationClass.RelationClassData relationClassData) {
        this.relationClassData = relationClassData;
    }

    @Override
    public void setName(RelationClass.RelationName name) {
        this.relationName = Optional.ofNullable(name);
    }

    @Override
    public <D extends Data<RelationClass.RelationClassIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D)  new RelationClassDataImpl(
                relationClassData.getIdentity(),
                version,
                this.relationName.orElse(relationClassData.getName()),
                relationClassData.getInstanceClassIdentity(RelationClass.RelationDirection.RELATION_FROM),
                relationClassData.getInstanceClassIdentity(RelationClass.RelationDirection.RELATION_TO),
                relationClassData.getRelationType());
    }
}
