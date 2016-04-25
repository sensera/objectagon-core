package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.service.Service;
import org.objectagon.core.storage.*;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-11-01.
 */
public interface EntityWorker extends BasicWorker {

    TaskBuilder getTaskBuilder();

    Transaction currentTransaction();

    PersistenceServiceProtocol.Send createPersistenceServiceProtocolSend(Name target);

    EntityServiceProtocol.Send createEntityServiceProtocolSend(Service.ServiceName serviceAddress, Name target);

    TransactionManagerProtocol.Send createTransactionManagerProtocolSend(Transaction transaction);

}
