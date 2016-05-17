package org.objectagon.core.msg.address;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;

import java.util.Objects;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardAddress implements Address {
    protected final Server.ServerId serverId;
    protected final long timestamp;
    protected final long addressId;

    public static StandardAddress standard(Server.ServerId serverId, long timestamp, long addressId) { return new StandardAddress(serverId, timestamp, addressId);}
    public static StandardAddress fromConfig(Receiver.AddressConfigurationParameters addressConfigurationParameters) {
        return new StandardAddress(addressConfigurationParameters.getServerId(), addressConfigurationParameters.getTimeStamp(), addressConfigurationParameters.getId());
    }

    protected StandardAddress(Server.ServerId serverId, long timestamp, long addressId) {
        this.serverId = serverId;
        this.timestamp = timestamp;
        this.addressId = addressId;
    }

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(Server.SERVER_ID).set(serverId);
        builderItem.create(TIMESTAMP).set(timestamp);
        builderItem.create(ADDRESS_ID).set(addressId);
    }

    public Server.ServerId getServerId() {
        return serverId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getAddressId() {
        return addressId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StandardAddress)) return false;
        StandardAddress that = (StandardAddress) o;
        return getTimestamp() == that.getTimestamp() &&
                getAddressId() == that.getAddressId() &&
                Objects.equals(getServerId(), that.getServerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerId(), getTimestamp(), getAddressId());
    }

    @Override
    public String toString() {
        return "StandardAddress{" +
                "serverId=" + serverId +
                ", timestamp=" + timestamp +
                ", addressId=" + addressId +
                '}';
    }
}
