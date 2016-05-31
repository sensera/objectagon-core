package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.service.Service;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionManagerProtocol;
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

    InstanceProtocol.Internal createInstanceProtocolInternal(Instance.InstanceIdentity instanceIdentity) ;

    InstanceProtocol.Send createInstanceProtocol(Instance.InstanceIdentity instanceIdentity) ;
}
