package org.objectagon.core.msg.address;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;

import java.util.Objects;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardAddress implements Address {
    private Server.ServerId serverId;
    private Receiver.CtrlId ctrlId;
    private long addressId;

    public static StandardAddress standard(Server.ServerId serverId, Receiver.CtrlId ctrlId, long addressId) { return new StandardAddress(serverId, ctrlId, addressId);}

    protected StandardAddress(Server.ServerId serverId, Receiver.CtrlId ctrlId, long addressId) {
        this.serverId = serverId;
        this.ctrlId = ctrlId;
        this.addressId = addressId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardAddress that = (StandardAddress) o;
        return Objects.equals(addressId, that.addressId) &&
                Objects.equals(serverId, that.serverId) &&
                Objects.equals(ctrlId, that.ctrlId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, ctrlId, addressId);
    }

    @Override
    public String toString() {
        return addressId+":"+ctrlId+"@"+serverId;
    }
}
