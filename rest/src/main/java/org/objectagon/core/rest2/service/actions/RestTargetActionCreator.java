package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.rest2.service.map.RestActionsMap;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2017-02-26.
 */
public class RestTargetActionCreator<U extends Protocol.Send> implements RestActionsMap.AddAction<U> {
    protected RestServiceActionLocatorImpl restServiceActionLocator;
    protected Address target;

    public RestTargetActionCreator(RestServiceActionLocatorImpl restServiceActionLocator, Address target) {
        this.restServiceActionLocator = restServiceActionLocator;
        this.target = target;
    }

    @Override
    public void addRestAction(RestActionsMap.Action restAction, RestActionsMap.CreateSendMessageAction<U> send) {
        restServiceActionLocator.addRestAction(
                createRestAction(restAction, restAction.getProtocol(), send),
                restAction.getMethod(),
                restAction);
    }

    protected RestServiceActionLocator.RestAction createRestAction(
            Task.TaskName taskName,
            Protocol.ProtocolName protocolName,
            RestActionsMap.CreateSendMessageAction<U> send) {
        return new AbstractTargetSessionRestAction<>(taskName, protocolName, target, send);
    }

}
