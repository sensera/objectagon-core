package org.objectagon.core.object.relation.data;

import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationDataChangeImpl implements Relation.RelationDataChange {

    private Relation.RelationData relationData;
    private Optional<Instance.InstanceIdentity> instanceIdentity = Optional.empty();

    public RelationDataChangeImpl(Relation.RelationData relationData) {
        this.relationData = relationData;
    }

    @Override
    public <D extends Data<Relation.RelationIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D) new RelationDataImpl(
                relationData.getIdentity(),
                version,
                relationData.getRelationClassIdentity(),
                instanceIdentity.orElse(relationData.getInstanceIdentity(RelationClass.RelationDirection.RELATION_FROM)),
                instanceIdentity.orElse(relationData.getInstanceIdentity(RelationClass.RelationDirection.RELATION_TO))
        );
    }

    @Override
    public Relation.RelationDataChange setRelatedInstance(Instance.InstanceIdentity instanceIdentity) {
        this.instanceIdentity = Optional.of(instanceIdentity);
        return this;
    }
}
