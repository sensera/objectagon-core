package org.objectagon.core.object.relation;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.RelationClass;
import lombok.*;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.relationclass.RelationDirectionUtil;

/**
 * Created by christian on 2016-03-07.
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RelationIdentityImpl extends StandardAddress implements Relation.RelationIdentity{
    final RelationClass.RelationClassIdentity relationClassIdentity;
    final Instance.InstanceIdentity instanceIdentityFrom;
    final Instance.InstanceIdentity instanceIdentityTo;

    @Override
    public Instance.InstanceIdentity getInstanceIdentity(RelationClass.RelationDirection relationDirection) {
        return RelationDirectionUtil.create(relationDirection).getInstance(()-> instanceIdentityFrom, ()->instanceIdentityTo);
    }

    public static RelationIdentityImpl identity(Instance.InstanceIdentity instanceIdentityFrom, Instance.InstanceIdentity instanceIdentityTo, RelationClass.RelationClassIdentity relationClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        return new RelationIdentityImpl(instanceIdentityFrom, instanceIdentityTo, relationClassIdentity, serverId, timestamp, addressId);
    }

    public RelationIdentityImpl(Instance.InstanceIdentity instanceIdentityFrom, Instance.InstanceIdentity instanceIdentityTo, RelationClass.RelationClassIdentity relationClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceIdentityFrom = instanceIdentityFrom;
        this.instanceIdentityTo = instanceIdentityTo;
        this.relationClassIdentity = relationClassIdentity;
    }
}
