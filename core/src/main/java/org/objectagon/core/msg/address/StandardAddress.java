package org.objectagon.core.msg.address;

import lombok.*;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2015-10-06.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardAddress implements Address {
    protected final Server.ServerId serverId;
    protected final long timestamp;
    protected final long addressId;

    public static StandardAddress standard(Server.ServerId serverId, long timestamp, long addressId) { return new StandardAddress(serverId, timestamp, addressId);}

}
