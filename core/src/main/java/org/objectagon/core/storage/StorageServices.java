package org.objectagon.core.storage;

import org.objectagon.core.service.Service;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.storage.persistence.PersistenceServiceProtocolImpl;
import org.objectagon.core.storage.transaction.TransactionServiceProtocolImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.objectagon.core.Server;
import org.objectagon.core.storage.transaction.TransactionService;

/**
 * Created by christian on 2016-03-17.
 */
@RequiredArgsConstructor(staticName = "create")
@Getter
public class StorageServices {
    private final Server server;

    Service.ServiceName persistenceServiceName;
    Service.ServiceName transactionServiceName;

    public StorageServices registerAt() {
        PersistenceService.registerAt(server);
        PersistenceServiceProtocolImpl.registerAtServer(server);
        TransactionService.registerAt(server);
        TransactionServiceProtocolImpl.registerAtServer(server);
        return this;
    }

    public StorageServices createReceivers() {
        persistenceServiceName = server.<Service.ServiceName,PersistenceService>createReceiver(PersistenceService.NAME, null).getAddress();
        transactionServiceName = server.<Service.ServiceName,TransactionService>createReceiver(TransactionService.NAME, null).getAddress();
        return this;
    }
}
