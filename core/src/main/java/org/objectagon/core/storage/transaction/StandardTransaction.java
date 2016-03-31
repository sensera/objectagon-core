package org.objectagon.core.storage.transaction;

import lombok.ToString;
import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.storage.Transaction;

/**
 * Created by christian on 2016-02-26.
 */
@ToString(callSuper = true)
public class StandardTransaction extends StandardAddress implements Transaction {

    public StandardTransaction(Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
    }



}
