package org.objectagon.core.storage.standard;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.storage.Identity;

/**
 * Created by christian on 2015-11-26.
 */
public class StandardIdentity extends StandardAddress implements Identity {

    public static StandardIdentity standardIdentity(Server.ServerId serverId, long timestamp, long addressId) { return new StandardIdentity(serverId, timestamp, addressId);}

    public StandardIdentity(Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
    }
}
