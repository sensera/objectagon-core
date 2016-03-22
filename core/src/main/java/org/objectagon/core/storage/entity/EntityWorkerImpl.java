package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.task.TaskBuilder;


/**
 * Created by christian on 2015-11-01.
 */
public class EntityWorkerImpl extends BasicWorkerImpl implements EntityWorker {

    public EntityWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
    }

    @Override
    public TaskBuilder getTaskBuilder() {
        throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED);
    }

    @Override
    public Transaction currentTransaction() {
        return getWorkerContext().getHeader(Transaction.TRANSACTION).asAddress();
    }

    public EntityServiceProtocol.Send createEntityServiceProtocolSend(Service.ServiceName serviceAddress, Name target) {
        return getWorkerContext()
                .<Protocol.ProtocolAddress,EntityServiceProtocol>createReceiver(
                        EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL,
                        null)
                .createSend(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE_ADDRESS, target));
    }

    public PersistenceServiceProtocol.Send createPersistenceServiceProtocolSend(Name target) {
        return getWorkerContext()
                .<Protocol.ProtocolAddress,PersistenceServiceProtocol>createReceiver(PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL, null)
                .createSend(() -> getWorkerContext().createRelayComposer(NameServiceImpl.NAME_SERVICE_ADDRESS, target));
    }


}
