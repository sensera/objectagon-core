package org.objectagon.core.object.instance;

import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import lombok.Getter;
import org.objectagon.core.Server;

/**
 * Created by christian on 2015-10-18.
 */
@Getter
public class InstanceIdentityImpl extends StandardAddress implements Instance.InstanceIdentity {

    InstanceClass.InstanceClassIdentity instanceClassIdentity;

    public InstanceIdentityImpl(InstanceClass.InstanceClassIdentity instanceClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceClassIdentity = instanceClassIdentity;
    }
}
