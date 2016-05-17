package org.objectagon.core.storage;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceProtocol;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.entity.EntityProtocolImpl;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.storage.persistence.PersistenceServiceProtocolImpl;
import org.objectagon.core.storage.search.SearchService;
import org.objectagon.core.storage.search.SearchServiceProtocolImpl;
import org.objectagon.core.storage.transaction.TransactionManagerImpl;
import org.objectagon.core.storage.transaction.TransactionManagerProtocolImpl;
import org.objectagon.core.storage.transaction.TransactionService;
import org.objectagon.core.storage.transaction.TransactionServiceProtocolImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2016-03-17.
 */
public class StorageServices {
    public static Name SEARCH_NAME_CONFIG = StandardName.name("SEARCH_NAME_CONFIG");

    public static Name SEARCH_NAME_EVENT = StandardName.name("SEARCH_NAME_EVENT");

    public static StorageServices create(Server server) {
        return new StorageServices(server);
    }

    enum InitTasks implements Task.TaskName {
        InitStorageServicesTasks,
        StartSearchProtocol, RegisterNameTask
    }

    private final Server server;

    Service.ServiceName persistenceServiceName;
    Service.ServiceName transactionServiceName;
    Service.ServiceName searchServiceName;

    public Server getServer() {
        return server;
    }

    public Service.ServiceName getPersistenceServiceName() {
        return persistenceServiceName;
    }

    public Service.ServiceName getTransactionServiceName() {
        return transactionServiceName;
    }

    public Service.ServiceName getSearchServiceName() {
        return searchServiceName;
    }

    private StorageServices(Server server) {
        this.server = server;
    }

    public StorageServices registerAt() {
        PersistenceService.registerAt(server);
        PersistenceServiceProtocolImpl.registerAtServer(server);
        TransactionService.registerAt(server);
        TransactionServiceProtocolImpl.registerAtServer(server);
        TransactionManagerImpl.registerAt(server);
        TransactionManagerProtocolImpl.registerAtServer(server);
        SearchService.registerAt(server);
        SearchServiceProtocolImpl.registerAtServer(server);
        EntityProtocolImpl.registerAtServer(server);
        return this;
    }

    public StorageServices createReceivers() {
        persistenceServiceName = server.<Service.ServiceName,PersistenceService>createReceiver(PersistenceService.NAME).getAddress();
        transactionServiceName = server.<Service.ServiceName,TransactionService>createReceiver(TransactionService.NAME).getAddress();
        searchServiceName = server.<Service.ServiceName,SearchService>createReceiver(SearchService.NAME).getAddress();
        return this;
    }

    public void initialize(TaskBuilder.SequenceBuilder sequence) {
        Server.AliasCtrl aliasCtrl = (Server.AliasCtrl) this.server;

        sequence.protocol(
                ServiceProtocol.SERVICE_PROTOCOL,
                searchServiceName,
                ServiceProtocol.Send::startService
        );
        sequence.<NameServiceProtocol.Send>protocol(
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                () -> aliasCtrl.lookupAddressByAlias(NameServiceImpl.NAME_SERVICE).get(),
                session -> session.registerName(persistenceServiceName, PersistenceService.NAME)
        );
        sequence.<NameServiceProtocol.Send>protocol(
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                () -> aliasCtrl.lookupAddressByAlias(NameServiceImpl.NAME_SERVICE).get(),
                session -> session.registerName(transactionServiceName, TransactionService.NAME)
        );
    }

}
