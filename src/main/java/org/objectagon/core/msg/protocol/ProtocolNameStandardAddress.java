package org.objectagon.core.msg.protocol;

import lombok.ToString;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;

/**
 * Created by christian on 2016-01-26.
 */
@ToString
public class ProtocolNameStandardAddress extends ProtocolAddressImpl implements Protocol.SenderAddress {

    private final long timestamp;
    private final long addressId;

    ProtocolNameStandardAddress(Protocol.ProtocolName protocolName, Server.ServerId serverId, long timestamp, long addressId) {
        super(protocolName, serverId);
        this.timestamp = timestamp;
        this.addressId = addressId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ProtocolNameStandardAddress) {
            ProtocolNameStandardAddress protocolNameStandardAddress = (ProtocolNameStandardAddress) o;
            return super.equals(o)
                    && timestamp == protocolNameStandardAddress.timestamp
                    && addressId == protocolNameStandardAddress.addressId;
        }
        return super.equals(o);
    }
}
