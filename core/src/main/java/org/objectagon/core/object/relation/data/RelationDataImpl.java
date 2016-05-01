package org.objectagon.core.object.relation.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.*;
import org.objectagon.core.object.relationclass.RelationDirectionUtil;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationDataImpl extends AbstractData<Relation.RelationIdentity, StandardVersion> implements Relation.RelationData {

    public static Relation.RelationData create(Relation.RelationIdentity relationIdentity, StandardVersion standardVersion) {
        return new RelationDataImpl(
                relationIdentity,
                standardVersion,
                relationIdentity.getRelationClassIdentity(),
                relationIdentity.getInstanceIdentity(RelationClass.RelationDirection.RELATION_FROM),
                relationIdentity.getInstanceIdentity(RelationClass.RelationDirection.RELATION_TO)
        );
    }

    private RelationClass.RelationClassIdentity relationClassIdentity;
    private Instance.InstanceIdentity instanceIdentityFrom;
    private Instance.InstanceIdentity instanceIdentityTo;

    RelationDataImpl(
            Relation.RelationIdentity identity,
            StandardVersion version,
            RelationClass.RelationClassIdentity relationClassIdentity,
            Instance.InstanceIdentity instanceIdentityFrom,
            Instance.InstanceIdentity instanceIdentityTo) {
        super(identity, version);
        this.relationClassIdentity = relationClassIdentity;
        this.instanceIdentityFrom = instanceIdentityFrom;
        this.instanceIdentityTo = instanceIdentityTo;
    }

    @Override
    public RelationClass.RelationClassIdentity getRelationClassIdentity() {
        return relationClassIdentity;
    }

    @Override
    public Instance.InstanceIdentity getInstanceIdentity(RelationClass.RelationDirection relationDirection) {
        return RelationDirectionUtil.create(relationDirection).getInstance(() -> instanceIdentityFrom, () -> instanceIdentityTo);
    }

    @Override
    public <C extends Data.Change<Relation.RelationIdentity, StandardVersion>> C change() {
        return (C) new RelationDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        //TODO Fix
        return Arrays.asList(
                MessageValue.address(RelationClass.RELATION_CLASS_IDENTITY, relationClassIdentity),
                MessageValue.address(Instance.INSTANCE_IDENTITY, instanceIdentityFrom),
                MessageValue.address(Instance.INSTANCE_IDENTITY, instanceIdentityTo)
        );

    }
}
