package org.objectagon.core.object.relation.data;

import org.objectagon.core.object.Instance;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationDataImpl extends AbstractData<Relation.RelationIdentity, StandardVersion> implements Relation.RelationData {

    public static Relation.RelationData create(Relation.RelationIdentity relationIdentity, StandardVersion standardVersion) {
        return new RelationDataImpl(
                relationIdentity,
                standardVersion,
                relationIdentity.getRelationClassIdentity(),
                relationIdentity.getInstanceIdentity()
        );
    }

    private RelationClass.RelationClassIdentity relationClassIdentity;
    private Instance.InstanceIdentity instanceIdentity;

    RelationDataImpl(
            Relation.RelationIdentity identity,
            StandardVersion version,
            RelationClass.RelationClassIdentity relationClassIdentity,
            Instance.InstanceIdentity instanceIdentity) {
        super(identity, version);
        this.relationClassIdentity = relationClassIdentity;
        this.instanceIdentity = instanceIdentity;
    }

    @Override
    public RelationClass.RelationClassIdentity getRelationClassIdentity() {
        return relationClassIdentity;
    }

    @Override
    public Instance.InstanceIdentity getInstanceIdentity() {
        return instanceIdentity;
    }

    @Override
    public <C extends Data.Change<Relation.RelationIdentity, StandardVersion>> C change() {
        return (C) new RelationDataChangeImpl(this);
    }

}
