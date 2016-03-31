package org.objectagon.core.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.storage.persistence.PersistenceServiceProtocolImpl;
import org.objectagon.core.storage.transaction.TransactionManagerImpl;
import org.objectagon.core.storage.transaction.TransactionManagerProtocolImpl;
import org.objectagon.core.storage.transaction.TransactionService;
import org.objectagon.core.storage.transaction.TransactionServiceProtocolImpl;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-03-17.
 */
@RequiredArgsConstructor(staticName = "create")
@Getter
public class StorageServices {
    enum InitTasks implements Task.TaskName {
        InitStorageServicesTasks;
    }

    private final Server server;

    Service.ServiceName persistenceServiceName;
    Service.ServiceName transactionServiceName;

    public StorageServices registerAt() {
        PersistenceService.registerAt(server);
        PersistenceServiceProtocolImpl.registerAtServer(server);
        TransactionService.registerAt(server);
        TransactionServiceProtocolImpl.registerAtServer(server);
        TransactionManagerImpl.registerAt(server);
        TransactionManagerProtocolImpl.registerAtServer(server);
        return this;
    }

    public StorageServices createReceivers() {
        persistenceServiceName = server.<Service.ServiceName,PersistenceService>createReceiver(PersistenceService.NAME, null).getAddress();
        transactionServiceName = server.<Service.ServiceName,TransactionService>createReceiver(TransactionService.NAME, null).getAddress();
        return this;
    }

    public Task initialize() {
        Optional<Address> nameServiceAddress = ((Server.AliasCtrl)server).lookupAddressByAlias(NameServiceImpl.NAME_SERVICE);
        return new ProtocolTask<NameServiceProtocol.Send>(
                (Receiver.ReceiverCtrl) server,
                InitTasks.InitStorageServicesTasks,
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress.get(), session -> session.registerName(persistenceServiceName, PersistenceService.NAME));
    }
}
