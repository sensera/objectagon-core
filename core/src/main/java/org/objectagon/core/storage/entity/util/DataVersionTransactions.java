package org.objectagon.core.storage.entity.util;

import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.Version;

import java.util.Objects;

/**
 * Created by christian on 2016-03-31.
 */
public class DataVersionTransactions<I extends Identity, V extends Version> {
    final DataVersion<I,V> dataVersion;

    public static <I extends Identity, V extends Version> DataVersionTransactions<I,V> create(DataVersion<I, V> dataVersion) { return new DataVersionTransactions<>(dataVersion);}

    public DataVersionTransactions(DataVersion<I, V> dataVersion) {
        this.dataVersion = dataVersion;
    }

    public boolean transactionExists(Transaction transaction) {
        return dataVersion.rootNode().isPresent()
                && transactionExists(dataVersion.rootNode().get(), transaction);
    }

    private boolean transactionExists(DataVersion.TransactionVersionNode<V> vTransactionVersionNode, Transaction transaction) {
        return  Objects.equals(vTransactionVersionNode.getTransaction(),transaction) ? true :
                vTransactionVersionNode.getNextVersion()
                .map(vTransactionVersionNode1 -> transactionExists(vTransactionVersionNode, transaction))
                .orElse(false);
    }
}
