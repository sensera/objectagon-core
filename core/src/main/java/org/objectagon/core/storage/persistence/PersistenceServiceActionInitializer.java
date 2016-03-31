package org.objectagon.core.storage.persistence;

import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.storage.*;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-02-23.
 */
public interface PersistenceServiceActionInitializer extends Reactor.ActionInitializer {
    void pushData(Identity identity, Version version, Data data);

    void pushDataVersion(Identity identity, Version version, DataVersion dataVersion);

    Optional<Data> getData(Identity identity, Version version);

    Optional<DataVersion> getDataVersion(Identity identity, Version version);

    void all(Identity identity, Consumer<Data> allVersions);

    void putTransactionData(Identity identity, Version version, TransactionManager.TransactionData transactionData);
}
