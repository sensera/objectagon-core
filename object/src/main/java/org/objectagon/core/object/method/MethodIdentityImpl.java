package org.objectagon.core.object.method;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Method;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodIdentityImpl extends StandardAddress implements Method.MethodIdentity {

    public MethodIdentityImpl(Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
    }

    @Override
    public String toString() {
        return "MethodIdentityImpl{} " + super.toString();
    }

}
