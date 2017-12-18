package org.objectagon.core.storage.transaction.data;

import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionManager;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-03-29.
 */
public class TransactionDataChangeImpl implements TransactionManager.TransactionDataChange {

    private final TransactionManager.TransactionData transactionData;
    private List<Consumer<Set<Identity>>> changes = new ArrayList<>();
    private Optional<Transaction> extendsTransaction = Optional.empty();

    public TransactionDataChangeImpl(TransactionManager.TransactionData transactionData) {
        this.transactionData = transactionData;
    }

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
    public TransactionManager.TransactionDataChange setExtendsTransaction(Transaction transaction) {
        extendsTransaction = Optional.ofNullable(transaction);
        return this;
    }

    @Override
    public <D extends Data<Transaction, StandardVersion>> D create(StandardVersion version) {
        Set<Identity> newList = transactionData.getIdentities().collect(Collectors.toSet());
        changes.forEach(listConsumer -> listConsumer.accept(newList));
        return (D) new TransactionDataImpl(
                transactionData.getIdentity(),
                version,
                newList,
                extendsTransaction.isPresent() ? extendsTransaction : transactionData.getExtendsTransaction()
        );
    }
}
