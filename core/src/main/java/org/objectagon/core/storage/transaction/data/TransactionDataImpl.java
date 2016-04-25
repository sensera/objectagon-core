package org.objectagon.core.storage.transaction.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionManager;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

/**
 * Created by christian on 2016-03-29.
 */
public class TransactionDataImpl extends AbstractData<Transaction,StandardVersion> implements TransactionManager.TransactionData {

    public static TransactionDataImpl create(Transaction transaction, StandardVersion version) {
        return new TransactionDataImpl(transaction, version, emptyList(), Optional.empty());
    }

    private List<Identity> identities;
    private Optional<Transaction> extendsTransaction;

    @Override
    public Stream<Identity> getIdentities() {
        return identities.stream();
    }

    @Override
    public Optional<Transaction> getExtendsTransaction() {
        return extendsTransaction;
    }

    TransactionDataImpl(Transaction identity, StandardVersion version, List<Identity> identities, Optional<Transaction> extendsTransaction) {
        super(identity, version);
        this.identities = identities;
        this.extendsTransaction = extendsTransaction;
    }

    @Override
    public <C extends Change<Transaction, StandardVersion>> C change() {
        return (C) new TransactionDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        return identities.stream().map(MessageValue::address).collect(Collectors.toList());
    }
}
