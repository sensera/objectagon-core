package org.objectagon.core.object.relationclass;

import lombok.Getter;
import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;

/**
 * Created by christian on 2015-10-18.
 */
@Getter
public class RelationClassIdentityImpl extends StandardAddress implements RelationClass.RelationClassIdentity {

    private InstanceClass.InstanceClassIdentity instanceClassIdentity;
    private RelationClass.RelationType relationType;

    public RelationClassIdentityImpl(InstanceClass.InstanceClassIdentity instanceClassIdentity, RelationClass.RelationType relationType, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceClassIdentity = instanceClassIdentity;
        this.relationType = relationType;
    }
}
