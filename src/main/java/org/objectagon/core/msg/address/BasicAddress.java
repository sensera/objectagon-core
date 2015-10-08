package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2015-10-06.
 */
public class BasicAddress implements Address {
    private long serverId;
    private long ctrlId;
    private long addressId;

    public BasicAddress(long serverId, long ctrlId, long addressId) {
        this.serverId = serverId;
        this.ctrlId = ctrlId;
        this.addressId = addressId;
    }
}
