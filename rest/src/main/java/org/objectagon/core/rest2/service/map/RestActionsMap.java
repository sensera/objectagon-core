package org.objectagon.core.rest2.service.map;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-26.
 */
public interface RestActionsMap<U extends Protocol.Send, A extends RestActionsMap.Action> {

    default Task throwNotImplementedSevereError(Action action) {
        throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED, MessageValue.name(action));
    }

    default <C extends CreateSendMessageAction<U>> void create(AddAction<U> addAction) {
        actions().forEach(action -> addAction.addRestAction(action, getAction(addAction, action)));
    }

    Stream<A> actions();

    <C extends CreateSendMessageAction<U>> RestActionsMap.CreateSendMessageAction<U> getAction(AddAction<U> restServiceActionCreator, A action);


    interface Action extends Task.TaskName, RestServiceActionLocator.RestPathPattern {
        RestServiceProtocol.Method getMethod();

        Protocol.ProtocolName getProtocol();

        default int identityTargetAtPathIndex() {
            return 0;
        }
    }

    interface AddAction<U extends Protocol.Send> {
        void addRestAction(RestActionsMap.Action restAction, CreateSendMessageAction<U> send);

        default <E extends Identity> E getIdAtIndex(int index, RestServiceActionLocator.RestPath restPath) {
            return restPath.values()
                    .filter(restPathValue -> restPathValue.getIndex() == index)
                    .findAny()
                    .orElseThrow(() -> new SevereError(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED))
                    .getIdentity()
                    .map(identity -> (E) identity)
                    .orElseThrow(() -> new SevereError(ErrorClass.REST_SERVICE, ErrorKind.ALIAS_NOT_FOUND, restPath.valueAtIndex(index)));
        }
    }

    default Task catchAlias(RestServiceActionLocator.IdentityStore identityStore, List<KeyValue<ParamName, Message.Value>> params, Message.Field field, Task task) {
        KeyValueUtil.create(params)
                .get(RestServiceProtocol.ALIAS_PARAM_NAME)
                .ifPresent(alias -> task.addSuccessAction(findAliasAndMessageIdentityToUpdateAlias(identityStore, field, alias)));
        return task;
    }

    default Task.SuccessAction findAliasAndMessageIdentityToUpdateAlias(RestServiceActionLocator.IdentityStore identityStore, Message.Field field, Message.Value alias) {
        return (messageName, values) -> identityStore.updateIdentity(MessageValueFieldUtil.create(values).getValueByField(field).asAddress(), alias.asValues().values().iterator().next().asText());
    }

    @FunctionalInterface
    interface CreateSendMessageAction<U extends Protocol.Send> {
        ProtocolTask.SendMessageAction<U> createProtocolSend(RestServiceActionLocator.IdentityStore identityStore, RestServiceActionLocator.RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data);
    }


}
