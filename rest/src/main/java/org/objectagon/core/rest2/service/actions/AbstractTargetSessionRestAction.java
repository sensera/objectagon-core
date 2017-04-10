package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.map.RestActionsMap;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.KeyValue;

import java.util.List;

/**
 * Created by christian on 2017-02-22.
 */
public class AbstractTargetSessionRestAction<U extends Protocol.Send> implements RestServiceActionLocator.RestAction {

    Task.TaskName taskName;
    Protocol.ProtocolName protocolName;
    private RestActionsMap.CreateSendMessageAction<U> createSendMessageAction;
    private Address target;

    public AbstractTargetSessionRestAction(Task.TaskName taskName, Protocol.ProtocolName protocolName, Address target, RestActionsMap.CreateSendMessageAction<U> createSendMessageAction) {
        this.taskName = taskName;
        this.protocolName = protocolName;
        this.target = target;
        this.createSendMessageAction = createSendMessageAction;
    }

    protected ProtocolTask.SendMessageAction createMessage(RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) {
        return createSendMessageAction.createProtocolSend(identityStore, restPath, params, data);
    }

    @Override
    public Task createTask(TaskBuilder taskBuilder, RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) throws UserException {
        return taskBuilder.protocol(taskName, protocolName, target, createMessage(identityStore, restPath, params, data)).create();
    }

    @Override
    public String toString() {
        return taskName.toString();
    }
}
