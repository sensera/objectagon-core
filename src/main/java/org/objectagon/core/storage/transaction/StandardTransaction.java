package org.objectagon.core.storage.transaction;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.storage.Transaction;

/**
 * Created by christian on 2016-02-26.
 */
public class StandardTransaction extends StandardAddress {

    public StandardTransaction(Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
    }

}
