package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.task.*;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityImpl<I extends Identity, D extends Data, V extends Version, W extends EntityWorker>  extends BasicReceiverImpl<I, W> implements Entity<I, D> {

    private DataVersionsContainer<D,V> data;
    private Address persistenceAddress;

    public EntityImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<I> initializer) {
        super.initialize(serverId, timestamp, id, initializer);
        EntityConfig entityConfig = initializer.initialize(getAddress());
        data = new DataVersionsContainer<>(entityConfig.getDatas());
        persistenceAddress = entityConfig.getPersistenceAddress();
    }

    protected D getDataByVersion(V version) {
        return data.getDataByVersion(version);
    }

    abstract protected V createVersionFromValue(Message.Value value);

    private void commit(EntityWorker entityWorker) {
        V version = createVersionFromValue(entityWorker.getValue(EntityProtocol.FieldName.VERSION));

        Data dataVersion = data.getDataByVersion(version);

        /*StandardTask.SendMessageAction<PersistenceServiceProtocol.Send> removeDataFromPersistence = session -> session.remove(dataVersion.getIdentity(), version);
        TaskBuilder.ChainedBuilder chain = entityWorker.getTaskBuilder().chain(
                TaskName.DELETE_DATA_VERSION_FROM_PERSISTANCE,
                PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL,
                persistenceAddress,
                removeDataFromPersistence
        );
        chain.next().action(
                TaskName.COMMIT_DATA_VERSION,
                () -> data.commit(version)
        );
        chain.start();*/
    }

    private void rollback(EntityWorker entityWorker) {
        V version = createVersionFromValue(entityWorker.getValue(EntityProtocol.FieldName.VERSION));

        /*Data dataVersion = data.getDataByVersion(version);

        StandardTask.SendMessageAction<PersistenceServiceProtocol.Send> removeDataFromPersistence = session -> session.remove(dataVersion.getIdentity(), version);
        TaskBuilder.ChainedBuilder chain = entityWorker.getTaskBuilder().chain(
                TaskName.DELETE_DATA_VERSION_FROM_PERSISTANCE,
                PersistenceServiceProtocol.PERSISTENCE_SERVICE_PROTOCOL,
                persistenceAddress,
                removeDataFromPersistence
        );
        chain.next().action(
                TaskName.ROLLBACK_DATA_VERSION,
                () -> data.rollback(version)
        );
        chain.start();*/
    }

    @Override
    protected void handle(W worker) {
        if (worker.messageHasName(EntityProtocol.MessageName.COMMIT)) {
            commit(worker);
            worker.setHandled();
        }
        if (worker.messageHasName(EntityProtocol.MessageName.ROLLBACK)) {
            rollback(worker);
            worker.setHandled();
        }
    }

    protected abstract class AbstractEntityWorker extends BasicWorkerImpl implements EntityWorker {
        public AbstractEntityWorker(WorkerContext workerContext) {
            super(workerContext);
        }

    }


}
