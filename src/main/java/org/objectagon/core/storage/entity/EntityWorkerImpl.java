package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;
import org.objectagon.core.storage.PersistenceServiceProtocol;
import org.objectagon.core.storage.persitence.PersistenceServiceProtocolImpl;
import org.objectagon.core.task.ChainedTask;
import org.objectagon.core.task.StandardTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-11-01.
 */
public class EntityWorkerImpl extends BasicWorkerImpl implements EntityWorker {

    public EntityWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
    }

    @Override
    public PersistenceServiceProtocol createPersistenceServiceProtocol(Address target) {
        return new PersistenceServiceProtocolImpl(new StandardComposer(target), getWorkerContext().getTransporter());
    }

    @Override
    public TaskBuilder getTaskBuilder() {
        return new TaskBuilder() {
            @Override
            public <U extends Protocol.Session> Builder<StandardTask> standard(Task.TaskName taskName, Protocol<U> protocol, Message.MessageName messageName) {
                return ;
            }

            @Override
            public <U extends Protocol.Session> Builder<ChainedTask> chain(Task.TaskName taskName, Protocol<U> protocol, Message.MessageName messageName) {
                return null;
            }
        };
    }
}
