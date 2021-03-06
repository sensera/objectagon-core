package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.rest2.service.map.RestActionsMap;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2017-02-26.
 */
public class RestActionCreator<U extends Protocol.Send> implements RestActionsMap.AddAction<U> {
    protected RestServiceActionLocatorImpl restServiceActionLocator;

    public RestActionCreator(RestServiceActionLocatorImpl restServiceActionLocator) {
        this.restServiceActionLocator = restServiceActionLocator;
    }

    @Override
    public void addRestAction(RestActionsMap.Action restAction, RestActionsMap.CreateSendMessageAction<U> send) {
        restServiceActionLocator.addRestAction(
                createRestAction(restAction, restAction.getProtocol(), restAction.identityTargetAtPathIndex(), send),
                restAction.getMethod(),
                restAction);
    }


    protected RestServiceActionLocator.RestAction createRestAction(
            Task.TaskName taskName,
            Protocol.ProtocolName protocolName,
            int identityTargetAtPathIndex,
            RestActionsMap.CreateSendMessageAction<U> send) {
        return new AbstractSessionRestAction<>(taskName, protocolName, identityTargetAtPathIndex, send);
    }

}
