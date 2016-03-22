package org.objectagon.core.object.relation;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.RelationClass;
import lombok.*;
import org.objectagon.core.object.Relation;

/**
 * Created by christian on 2016-03-07.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RelationIdentityImpl extends StandardAddress implements Relation.RelationIdentity{
    final RelationClass.RelationClassIdentity relationClassIdentity;
    final Instance.InstanceIdentity instanceIdentity;

    public static RelationIdentityImpl identity(Instance.InstanceIdentity instanceIdentity, RelationClass.RelationClassIdentity relationClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        return new RelationIdentityImpl(instanceIdentity, relationClassIdentity, serverId, timestamp, addressId);
    }

    public RelationIdentityImpl(Instance.InstanceIdentity instanceIdentity, RelationClass.RelationClassIdentity relationClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceIdentity = instanceIdentity;
        this.relationClassIdentity = relationClassIdentity;
    }
}
