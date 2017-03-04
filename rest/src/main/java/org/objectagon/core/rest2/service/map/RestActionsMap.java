package org.objectagon.core.rest2.service.map;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.service.actions.AbstractSessionRestAction;
import org.objectagon.core.rest2.service.actions.RestActionCreator;
import org.objectagon.core.task.Task;

import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-26.
 */
public interface RestActionsMap<E, A extends RestActionsMap.Action> {

    default Task throwNotImplementedSevereError() {
        throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED);
    }

    default void create(RestActionCreator restActionCreator) {
        actions().forEach(action -> restActionCreator.addRestAction(action, (AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send>) getAction(restActionCreator, action)));
    }

    Stream<A> actions();

    E getAction(RestActionCreator restActionCreator, A action);

    interface Action extends RestActionCreator.RestAction {}

}
