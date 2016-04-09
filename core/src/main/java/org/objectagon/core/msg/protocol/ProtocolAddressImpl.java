package org.objectagon.core.msg.protocol;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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