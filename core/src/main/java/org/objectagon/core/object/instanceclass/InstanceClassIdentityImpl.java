package org.objectagon.core.object.instanceclass;

import lombok.ToString;
import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.InstanceClass;

/**
 * Created by christian on 2016-02-28.
 */
@ToString(callSuper = true)
public class InstanceClassIdentityImpl extends StandardAddress implements InstanceClass.InstanceClassIdentity {

    public InstanceClassIdentityImpl(Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
    }
}
