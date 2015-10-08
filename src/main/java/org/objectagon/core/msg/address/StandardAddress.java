package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardAddress implements Address {
    private long serverId;
    private long ctrlId;
    private long addressId;

    public StandardAddress(long serverId, long ctrlId, long addressId) {
        this.serverId = serverId;
        this.ctrlId = ctrlId;
        this.addressId = addressId;
    }
}
