package org.objectagon.core.object.relationclass.data;

import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassDataImpl extends AbstractData<RelationClass.RelationClassIdentity, StandardVersion> implements RelationClass.RelationClassData {

    public static RelationClass.RelationClassData create(RelationClass.RelationClassIdentity relationClassIdentity, StandardVersion standardVersion) {
        return new RelationClassDataImpl(
                relationClassIdentity,
                standardVersion,
                relationClassIdentity.getInstanceClassIdentity(),
                relationClassIdentity.getRelationType());
    }

    private InstanceClass.InstanceClassIdentity instanceClassIdentity;
    private RelationClass.RelationType relationType;

    public RelationClassDataImpl(
            RelationClass.RelationClassIdentity identity,
            StandardVersion version,
            InstanceClass.InstanceClassIdentity instanceClassIdentity,
            RelationClass.RelationType relationType) {
        super(identity, version);
        this.instanceClassIdentity = instanceClassIdentity;
        this.relationType = relationType;
    }

    @Override public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
        return instanceClassIdentity;
    }
    @Override public RelationClass.RelationType getRelationType() {return relationType;}

    @Override
    public <C extends Change<RelationClass.RelationClassIdentity, StandardVersion>> C change() {
        return (C) new RelationClassDataChangeImpl(this);
    }

}
