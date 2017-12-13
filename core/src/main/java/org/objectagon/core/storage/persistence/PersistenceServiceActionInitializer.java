package org.objectagon.core.storage.persistence;

import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.storage.*;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-02-23.
 */
public interface PersistenceServiceActionInitializer extends Reactor.ActionInitializer {
    void pushData(Data data);

    Optional<Data> getData(Identity identity, Version version);

    Optional<DataRevision> getDataVersion(Identity identity, Version version);

    void all(Identity identity, Consumer<Data> allVersions);

    void all(Data.Type type, Consumer<Data> allVersions);

    void putTransactionData(Identity identity, Version version, TransactionManager.TransactionData transactionData);
}
