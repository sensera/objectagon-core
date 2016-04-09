package org.objectagon.core.storage.transaction.data;

import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionManager;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-03-29.
 */
@lombok.Data
public class TransactionDataChangeImpl implements TransactionManager.TransactionDataChange {

    private final TransactionManager.TransactionData transactionData;
    private List<Consumer<List<Identity>>> changes = new ArrayList<>();

    @Override
    public TransactionManager.TransactionDataChange add(Identity identity) {
        changes.add(list -> list.add(identity));
        return this;
    }

    @Override
    public TransactionManager.TransactionDataChange remove(Identity identity) {
        changes.add(list -> list.remove(identity));
        return this;
    }

    @Override
    public <D extends Data<Transaction, StandardVersion>> D create(StandardVersion version) {
        List<Identity> newList = transactionData.getIdentities().collect(Collectors.toList());
        changes.forEach(listConsumer -> listConsumer.accept(newList));
        return (D) new TransactionDataImpl(
                transactionData.getIdentity(),
                version,
                newList
        );
    }
}