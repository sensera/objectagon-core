package org.objectagon.core.object.instance;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;

/**
 * Created by christian on 2015-10-18.
 */
public class InstanceIdentityImpl extends StandardAddress implements Instance.InstanceIdentity {

    InstanceClass.InstanceClassIdentity instanceClassIdentity;

    @Override
    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
        return instanceClassIdentity;
    }

    public InstanceIdentityImpl(InstanceClass.InstanceClassIdentity instanceClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceClassIdentity = instanceClassIdentity;
    }

    @Override
    public String toString() {
        return "InstanceIdentityImpl{" +
                "instanceClassIdentity=" + instanceClassIdentity +
                "} " + super.toString();
    }
}
