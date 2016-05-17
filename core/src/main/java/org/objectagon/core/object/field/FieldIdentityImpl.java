package org.objectagon.core.object.field;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;

/**
 * Created by christian on 2016-03-04.
 */
public class FieldIdentityImpl extends StandardAddress implements Field.FieldIdentity {

    final private InstanceClass.InstanceClassIdentity instanceClassIdentity;

    @Override
    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
        return instanceClassIdentity;
    }

    public FieldIdentityImpl(InstanceClass.InstanceClassIdentity instanceClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceClassIdentity = instanceClassIdentity;
    }

    @Override
    public String toString() {
        return "FieldIdentityImpl{" +
                "instanceClassIdentity=" + instanceClassIdentity +
                "} " + super.toString();
    }
}
