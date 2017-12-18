package org.objectagon.core.storage.entity.util;

import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.Version;

import java.util.Objects;

/**
 * Created by christian on 2016-03-31.
 */
public class DataVersionTransactions<I extends Identity, V extends Version> {
    final DataRevision<I,V> dataRevision;

    public static <I extends Identity, V extends Version> DataVersionTransactions<I,V> create(DataRevision<I, V> dataRevision) { return new DataVersionTransactions<>(dataRevision);}

    public DataVersionTransactions(DataRevision<I, V> dataRevision) {
        this.dataRevision = dataRevision;
    }

    public boolean transactionExists(Transaction transaction) {
        return dataRevision.rootNode()
                .map(vTransactionVersionNode -> transactionExists(vTransactionVersionNode, transaction))
                .orElse(false);
    }

    private boolean transactionExists(DataRevision.TransactionVersionNode<V> vTransactionVersionNode, Transaction transaction) {
        return  equalsOrExtendsTRansaction(vTransactionVersionNode.getTransaction(),transaction) ? true :
                vTransactionVersionNode.getNextVersion()
                .map(vTransactionVersionNode1 -> transactionExists(vTransactionVersionNode, transaction))
                .orElse(false);
    }

    private boolean equalsOrExtendsTRansaction(Transaction origin, Transaction equalOrExtends) {
        return Objects.equals(origin,equalOrExtends)
                && origin.extendsTransaction()
                .map(extendedTrans -> equalsOrExtendsTRansaction(extendedTrans, equalOrExtends))
                .orElse(true);
    }
}
