package org.objectagon.core.storage.transaction;

import lombok.ToString;
import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.storage.Transaction;

import java.util.Optional;

/**
 * Created by christian on 2016-02-26.
 */
@ToString(callSuper = true)
public class StandardTransaction extends StandardAddress implements Transaction {

    public static StandardTransaction create(Server.ServerId serverId, long timestamp, long addressId) {
        return new StandardTransaction(serverId, timestamp, addressId, null);
    }

    public static StandardTransaction extend(Server.ServerId serverId, long timestamp, long addressId, Transaction extendsTransaction) {
        return new StandardTransaction(serverId, timestamp, addressId, extendsTransaction);
    }

    private Transaction extendsTransaction;

    private StandardTransaction(Server.ServerId serverId, long timestamp, long addressId, Transaction extendsTransaction) {
        super(serverId, timestamp, addressId);
        this.extendsTransaction = extendsTransaction;
    }

    @Override
    public Optional<Transaction> extendsTransaction() {
        return Optional.ofNullable(extendsTransaction);
    }
}
