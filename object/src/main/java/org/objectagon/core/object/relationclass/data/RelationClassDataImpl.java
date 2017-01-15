package org.objectagon.core.object.relationclass.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.relationclass.RelationDirectionUtil;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassDataImpl extends AbstractData<RelationClass.RelationClassIdentity, StandardVersion> implements RelationClass.RelationClassData {

    public static RelationClass.RelationClassData create(RelationClass.RelationClassIdentity relationClassIdentity, StandardVersion standardVersion) {
        return new RelationClassDataImpl(
                relationClassIdentity,
                standardVersion,
                relationClassIdentity.getInstanceClassIdentity(RelationClass.RelationDirection.RELATION_FROM),
                relationClassIdentity.getInstanceClassIdentity(RelationClass.RelationDirection.RELATION_TO),
                relationClassIdentity.getRelationType());
    }

    private InstanceClass.InstanceClassIdentity instanceClassIdentityFrom;
    private InstanceClass.InstanceClassIdentity instanceClassIdentityTo;
    private RelationClass.RelationType relationType;

    public RelationClassDataImpl(
            RelationClass.RelationClassIdentity identity,
            StandardVersion version,
            InstanceClass.InstanceClassIdentity instanceClassIdentityFrom,
            InstanceClass.InstanceClassIdentity instanceClassIdentityTo,
            RelationClass.RelationType relationType) {
        super(identity, version);
        this.instanceClassIdentityFrom = instanceClassIdentityFrom;
        this.instanceClassIdentityTo = instanceClassIdentityTo;
        this.relationType = relationType;
    }

    @Override public InstanceClass.InstanceClassIdentity getInstanceClassIdentity(RelationClass.RelationDirection relationDirection) {
        return RelationDirectionUtil.create(relationDirection).getInstanceClassIdentity(() -> instanceClassIdentityFrom, () -> instanceClassIdentityTo);
    }
    @Override public RelationClass.RelationType getRelationType() {return relationType;}

    @Override
    public <C extends Change<RelationClass.RelationClassIdentity, StandardVersion>> C change() {
        return (C) new RelationClassDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        //TODO Fix
        return Arrays.asList(
                MessageValue.address(RelationClass.RELATION_CLASS_IDENTITY, getIdentity()),
                MessageValue.address(RelationClass.INSTANCE_CLASS_FROM, instanceClassIdentityFrom),
                MessageValue.address(RelationClass.INSTANCE_CLASS_TO, instanceClassIdentityTo)
        );
    }
}
