package org.objectagon.core.rest2.service.map;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.service.actions.AbstractSessionRestAction;
import org.objectagon.core.rest2.service.actions.RestActionCreator;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-26.
 */
public interface RestActionsMap<E, A extends RestActionsMap.Action> {

    default Task throwNotImplementedSevereError(Action action) {
        throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED, MessageValue.name(action));
    }

    default void create(RestActionCreator restActionCreator) {
        actions().forEach(action -> restActionCreator.addRestAction(action, (AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send>) getAction(restActionCreator, action)));
    }

    Stream<A> actions();

    E getAction(RestActionCreator restActionCreator, A action);

    interface Action extends RestActionCreator.RestAction {}

    default Task catchAlias(RestServiceActionLocator.IdentityStore identityStore, List<KeyValue<ParamName, Message.Value>> params, Message.Field field, Task task) {
        KeyValueUtil.create(params)
                .get(RestServiceProtocol.ALIAS_PARAM_NAME)
                .ifPresent(alias -> task.addSuccessAction(findAliasAndMessageIdentityToUpdateAlias(identityStore, field, alias)));
        return task;
    }

    default Task.SuccessAction findAliasAndMessageIdentityToUpdateAlias(RestServiceActionLocator.IdentityStore identityStore, Message.Field field, Message.Value alias) {
        return (messageName, values) -> identityStore.updateIdentity(MessageValueFieldUtil.create(values).getValueByField(field).asAddress(), alias.asText());
    }

}
