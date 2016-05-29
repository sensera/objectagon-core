package org.objectagon.core.object.meta;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Meta;

/**
 * Created by christian on 2016-05-29.
 */
public class MetaIdentityImpl extends StandardAddress implements Meta.MetaIdentity {

    public MetaIdentityImpl(Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
    }

    @Override
    public String toString() {
        return "MetaIdentityImpl{} " + super.toString();
    }
}
