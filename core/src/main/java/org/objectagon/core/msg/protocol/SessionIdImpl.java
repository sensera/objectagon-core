package org.objectagon.core.msg.protocol;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.address.StandardAddress;

/**
 * Created by christian on 2017-01-25.
 */
public class SessionIdImpl extends StandardAddress implements Protocol.SessionId {

    public static SessionIdImpl create(Server.ServerId serverId, long timestamp, long addressId) { return new SessionIdImpl(serverId, timestamp, addressId);}

    private SessionIdImpl(Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
    }
}
