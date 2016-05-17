package org.objectagon.core.msg.protocol;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;

import java.util.Objects;

/**
 * Created by christian on 2016-01-26.
 */
public class ProtocolAddressImpl implements Protocol.ProtocolAddress {

    private final Protocol.ProtocolName protocolName;
    private final Server.ServerId serverId;

    @Override
    public ProtocolNameStandardAddress create(long timestamp, long addressId) {
        return new ProtocolNameStandardAddress(protocolName, serverId, timestamp, addressId);
    }

    public ProtocolAddressImpl(Protocol.ProtocolName protocolName, Server.ServerId serverId) {
        this.protocolName = protocolName;
        this.serverId = serverId;
    }

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(Server.SERVER_ID).set(serverId);
        builderItem.create(Protocol.PROTOCOL_NAME).set(protocolName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProtocolAddressImpl)) return false;
        ProtocolAddressImpl that = (ProtocolAddressImpl) o;
        return Objects.equals(protocolName, that.protocolName) &&
                Objects.equals(serverId, that.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolName, serverId);
    }

    @Override
    public String toString() {
        return "ProtocolAddressImpl{" +
                "protocolName=" + protocolName +
                ", serverId=" + serverId +
                '}';
    }
}
