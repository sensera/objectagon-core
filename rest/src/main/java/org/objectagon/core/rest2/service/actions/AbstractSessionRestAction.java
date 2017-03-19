package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.service.Service;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.KeyValue;

import java.util.List;

/**
 * Created by christian on 2017-02-22.
 */
public class AbstractSessionRestAction<U extends Protocol.Send> implements RestServiceActionLocator.RestAction {

    Task.TaskName taskName;
    Protocol.ProtocolName protocolName;
    Service.ServiceName target;
    private CreateSendMessageAction<U> createSendMessageAction;

    public AbstractSessionRestAction(Task.TaskName taskName, Protocol.ProtocolName protocolName, Service.ServiceName target, CreateSendMessageAction<U> createSendMessageAction) {
        this.taskName = taskName;
        this.protocolName = protocolName;
        this.target = target;
        this.createSendMessageAction = createSendMessageAction;
    }

    protected ProtocolTask.SendMessageAction createMessage(RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) {
        return createSendMessageAction.createProtocolSend(identityStore, restPath, params, data);
    }

    @Override
    public Task createTask(TaskBuilder taskBuilder, RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) {
        return taskBuilder.protocolRelay(taskName, protocolName, target, createMessage(identityStore, restPath, params, data)).create();
    }

    @FunctionalInterface
    public interface CreateSendMessageAction<U extends Protocol.Send> {
        ProtocolTask.SendMessageAction<U> createProtocolSend(RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data);
    }

    @Override
    public String toString() {
        return taskName.toString();
    }
}
