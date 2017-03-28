package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.rest2.service.map.RestActionsMap;
import org.objectagon.core.service.Service;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2017-02-26.
 */
public class RestServiceActionCreator<U extends Protocol.Send> implements RestActionsMap.AddAction<U> {
    protected RestServiceActionLocatorImpl restServiceActionLocator;
    protected Service.ServiceName target;

    public RestServiceActionCreator(RestServiceActionLocatorImpl restServiceActionLocator, Service.ServiceName target) {
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
        return new AbstractServiceSessionRestAction<>(taskName, protocolName, target, send);
    }

}
