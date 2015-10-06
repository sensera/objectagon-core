package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2015-10-06.
 */
public class BasicAddress implements Address {
    long serverId;
    long addressId;

    public BasicAddress(long serverId, long addressId) {
        this.serverId = serverId;
        this.addressId = addressId;
    }
}
