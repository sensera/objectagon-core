package org.objectagon.core.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceProtocol;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.storage.persistence.PersistenceServiceProtocolImpl;
import org.objectagon.core.storage.search.SearchService;
import org.objectagon.core.storage.search.SearchServiceProtocolImpl;
import org.objectagon.core.storage.transaction.TransactionManagerImpl;
import org.objectagon.core.storage.transaction.TransactionManagerProtocolImpl;
import org.objectagon.core.storage.transaction.TransactionService;
import org.objectagon.core.storage.transaction.TransactionServiceProtocolImpl;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.SequenceTask;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-03-17.
 */
@RequiredArgsConstructor(staticName = "create")
@Getter
public class StorageServices {
    public static Name SEARCH_NAME_CONFIG = StandardName.name("SEARCH_NAME_CONFIG");

    public static Name SEARCH_NAME_EVENT = StandardName.name("SEARCH_NAME_EVENT");

    enum InitTasks implements Task.TaskName {
        InitStorageServicesTasks,
        StartSearchProtocol, RegisterNameTask
    }

    private final Server server;

    Service.ServiceName persistenceServiceName;
    Service.ServiceName transactionServiceName;
    Service.ServiceName searchServiceName;

    public StorageServices registerAt() {
        PersistenceService.registerAt(server);
        PersistenceServiceProtocolImpl.registerAtServer(server);
        TransactionService.registerAt(server);
        TransactionServiceProtocolImpl.registerAtServer(server);
        TransactionManagerImpl.registerAt(server);
        TransactionManagerProtocolImpl.registerAtServer(server);
        SearchService.registerAt(server);
        SearchServiceProtocolImpl.registerAtServer(server);
        return this;
    }

    public StorageServices createReceivers() {
        persistenceServiceName = server.<Service.ServiceName,PersistenceService>createReceiver(PersistenceService.NAME).getAddress();
        transactionServiceName = server.<Service.ServiceName,TransactionService>createReceiver(TransactionService.NAME).getAddress();
        searchServiceName = server.<Service.ServiceName,SearchService>createReceiver(SearchService.NAME).getAddress();
        return this;
    }

    public Task initialize() {
        Optional<Address> nameServiceAddress = ((Server.AliasCtrl)server).lookupAddressByAlias(NameServiceImpl.NAME_SERVICE);
        SequenceTask sequenceTask = new SequenceTask(
                (Receiver.ReceiverCtrl) server,
                InitTasks.InitStorageServicesTasks);

        sequenceTask.add(new ProtocolTask<NameServiceProtocol.Send>(
                (Receiver.ReceiverCtrl) server,
                InitTasks.RegisterNameTask,
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress.get(), session -> session.registerName(persistenceServiceName, PersistenceService.NAME))
        );

        sequenceTask.add(
                new ProtocolTask<>(
                    (Receiver.ReceiverCtrl) server,
                    InitTasks.StartSearchProtocol,
                    ServiceProtocol.SERVICE_PROTOCOL,
                    searchServiceName,
                    ServiceProtocol.Send::startService
                )
        );


        return sequenceTask;
    }

}
