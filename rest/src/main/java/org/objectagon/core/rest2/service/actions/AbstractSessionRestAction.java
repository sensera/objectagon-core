package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
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
public class AbstractSessionRestAction<U extends Protocol.Send> implements RestServiceActionLocator.RestAction {

    Task.TaskName taskName;
    Protocol.ProtocolName protocolName;
    private RestActionsMap.CreateSendMessageAction<U> createSendMessageAction;
    private int identityTargetAtPathIndex;

    public AbstractSessionRestAction(Task.TaskName taskName, Protocol.ProtocolName protocolName, int identityTargetAtPathIndex, RestActionsMap.CreateSendMessageAction<U> createSendMessageAction) {
        this.taskName = taskName;
        this.protocolName = protocolName;
        this.identityTargetAtPathIndex = identityTargetAtPathIndex;
        this.createSendMessageAction = createSendMessageAction;
    }

    protected ProtocolTask.SendMessageAction createMessage(RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) {
        return createSendMessageAction.createProtocolSend(identityStore, restPath, params, data);
    }

    @Override
    public Task createTask(TaskBuilder taskBuilder, RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data) throws UserException {
        return restPath.getIdentityAtIndex(identityTargetAtPathIndex)
                .map(target -> taskBuilder.protocol(taskName, protocolName, target, createMessage(identityStore, restPath, params, data)).create())
                .orElseThrow(() -> new UserException(
                        ErrorClass.REST_SERVICE,
                        ErrorKind.TARGET_IS_NULL,
                        MessageValue.name(taskName),
                        MessageValue.name(protocolName),
                        MessageValue.number(NamedField.number("identityTargetAtPathIndex"), (long) identityTargetAtPathIndex)));
    }

    @Override
    public String toString() {
        return taskName.toString();
    }
}
