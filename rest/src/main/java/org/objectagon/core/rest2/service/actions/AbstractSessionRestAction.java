package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
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
    Address target;
    private CreateSendMessageAction createSendMessageAction;

    public AbstractSessionRestAction(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, CreateSendMessageAction createSendMessageAction) {
        this.taskName = taskName;
        this.protocolName = protocolName;
        this.target = target;
        this.createSendMessageAction = createSendMessageAction;
    }

    protected ProtocolTask.SendMessageAction createMessage(RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) {
        return createSendMessageAction.createProtocolSend(restPath, params, data);
    }

    @Override
    public Task createTask(TaskBuilder taskBuilder, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) {
        return taskBuilder.protocol(taskName, protocolName, target, createMessage(restPath, params, data)).create();
    }

    @FunctionalInterface
    public interface CreateSendMessageAction<U extends Protocol.Send> {
        ProtocolTask.SendMessageAction<U> createProtocolSend(RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data);
    }

}
