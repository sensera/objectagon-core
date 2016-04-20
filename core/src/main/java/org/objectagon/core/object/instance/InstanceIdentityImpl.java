package org.objectagon.core.object.instance;

import lombok.ToString;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import lombok.Getter;
import org.objectagon.core.Server;

/**
 * Created by christian on 2015-10-18.
 */
@Getter
@ToString(callSuper = true)
public class InstanceIdentityImpl extends StandardAddress implements Instance.InstanceIdentity {

    InstanceClass.InstanceClassIdentity instanceClassIdentity;

    public InstanceIdentityImpl(InstanceClass.InstanceClassIdentity instanceClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        if (instanceClassIdentity==null)
            throw new NullPointerException("instanceClassIdentity is null");
        this.instanceClassIdentity = instanceClassIdentity;
    }
}
