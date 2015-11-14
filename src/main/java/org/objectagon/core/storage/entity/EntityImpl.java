package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.persitence.PersistenceServiceProtocolImpl;
import org.objectagon.core.task.StandardTaskBuilder;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.AbstractTaskBuilder;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityImpl<I extends Identity, D extends Data, V extends Version, W extends EntityWorker>  extends BasicReceiverImpl<I, Entity.EntityCtrl<I>, W> implements Entity<I, D> {

    private DataVersions<D,V> data;

    public EntityImpl(EntityCtrl<I> entityCtrl, D data) {
        super(entityCtrl);
        this.data = new DataVersions<D,V>(data);
    }

    abstract protected V createVersionFromValue(Message.Value value);

    private Task.TaskCtrl createTaskCtrl() {
        return new Task.TaskCtrl() {
            @Override
            public void success(Message message) {

            }

            @Override
            public void failed(Message message) {

            }

            @Override
            public Address createNewAddress(Receiver receiver) {
                return getReceiverCtrl().createNewAddress(receiver);
            }

            @Override
            public void transport(Envelope envelope) {

            }
        };
    }

    private void commit(EntityWorker entityWorker) {
        V version = createVersionFromValue(entityWorker.getValue(EntityProtocol.FieldName.VERSION));
        PersistenceServiceProtocol persistenceServiceProtocol = entityWorker.createPersistenceServiceProtocol();
        persistenceServiceProtocol.createSession(getReceiverCtrl().getPersistenceAddress());

        Data dataVersion = data.getDataByVersion(version);

        TaskBuilder.ChainedBuilder chain = entityWorker.getTaskBuilder().chain(
                TaskName.DELETE_DATA_VERSION_FROM_PERSISTANCE,
                persistenceServiceProtocol,
                getReceiverCtrl().getPersistenceAddress(),
                session -> session.remove(dataVersion.getIdentity(), version)
        );
        chain.next().action(
                TaskName.COMMIT_DATA_VERSION,
                () -> data.commit(version)
        );
        chain.start();
    }

    private void rollback(EntityWorker entityWorker) {
        V version = createVersionFromValue(entityWorker.getValue(EntityProtocol.FieldName.VERSION));
        PersistenceServiceProtocol persistenceServiceProtocol = entityWorker.createPersistenceServiceProtocol();
        persistenceServiceProtocol.createSession(getReceiverCtrl().getPersistenceAddress());

        Data dataVersion = data.getDataByVersion(version);

        TaskBuilder.ChainedBuilder chain = entityWorker.getTaskBuilder().chain(
                TaskName.DELETE_DATA_VERSION_FROM_PERSISTANCE,
                persistenceServiceProtocol,
                getReceiverCtrl().getPersistenceAddress(),
                session -> session.remove(dataVersion.getIdentity(), version)
        );
        chain.next().action(
                TaskName.ROLLBACK_DATA_VERSION,
                () -> data.rollback(version)
        );
        chain.start();
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

        @Override
        public PersistenceServiceProtocol createPersistenceServiceProtocol() {
            return new PersistenceServiceProtocolImpl(getWorkerContext().createStandardComposer(), getWorkerContext().getTransporter());
        }
    }


}
