package org.objectagon.core.msg.address;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.UnknownValue;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by christian on 2015-10-06.
 */
@EqualsAndHashCode
@ToString()
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardAddress implements Address {
    protected Server.ServerId serverId;
    protected long timestamp;
    protected long addressId;

    public static StandardAddress standard(Server.ServerId serverId, long timestamp, long addressId) { return new StandardAddress(serverId, timestamp, addressId);}

}
