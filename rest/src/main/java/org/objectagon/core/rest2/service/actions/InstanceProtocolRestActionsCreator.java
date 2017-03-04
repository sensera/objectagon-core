package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.service.map.InstanceProtocolRestActionsMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class InstanceProtocolRestActionsCreator implements InstanceProtocolRestActionsMap<AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send>,InstanceProtocolRestActionsMap.InstanceAction> {

    public AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send> getAction(RestActionCreator restActionCreator, InstanceProtocolRestActionsMap.InstanceAction action) {
        switch (action) {
            case GET_VALUE: return (restPath, params, data) -> session -> session.getValue(restActionCreator.getIdAtIndex(1, restPath));
            default: return (restPath, params, data) -> session -> throwNotImplementedSevereError();
        }
    }

    @Override
    public Stream<InstanceProtocolRestActionsMap.InstanceAction> actions() {
        return Arrays.stream(InstanceProtocolRestActionsMap.InstanceAction.values());
    }
}
