package org.objectagon.core.service;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;
import org.objectagon.core.task.ChainedTask;
import org.objectagon.core.task.StandardTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;

/**
 * Created by christian on 2015-10-13.
 */
public class ServiceWorkerImpl extends BasicWorkerImpl implements Service.ServiceWorker {
    final private ServiceProtocol serviceProtocol;

    public ServiceWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
        serviceProtocol = new ServiceProtocolImpl(workerContext.createStandardComposer(), workerContext.getTransporter());
    }

    public ServiceProtocol.Session createServiceProtocolSession() {
        return serviceProtocol.createSession(getWorkerContext().getSender());
    }

    public void success() {
        createStandardProtocolSession().replyOk();
    }

    public void failed(StandardProtocol.ErrorKind errorKind, String description) {
        createStandardProtocolSession().replyWithError(description, errorKind);
    }

    public void replyWithError(ServiceProtocol.ErrorKind errorKind) {
        createServiceProtocolSession().replyWithError(errorKind);
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
