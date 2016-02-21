package org.objectagon.core.msg.protocol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.StandardAddress;

/**
 * Created by christian on 2016-01-26.
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProtocolAddressImpl implements Protocol.ProtocolAddress {

    private Protocol.ProtocolName protocolName;
    private Server.ServerId serverId;

    @Override
    public ProtocolNameStandardAddress create(long timestamp, long addressId) {
        return new ProtocolNameStandardAddress(protocolName, serverId, timestamp, addressId);
    }
}
