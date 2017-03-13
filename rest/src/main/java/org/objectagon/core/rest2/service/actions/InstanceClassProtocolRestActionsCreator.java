package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.service.map.InstanceClassProtocolRestActionsMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class InstanceClassProtocolRestActionsCreator implements InstanceClassProtocolRestActionsMap<AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send>,InstanceClassProtocolRestActionsMap.InstanceClassAction> {

    public AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send> getAction(RestActionCreator restActionCreator, InstanceClassAction action) {
        switch (action) {
            case LIST_CLASSES: return (restPath, params, data) -> session -> session.getValue(restActionCreator.getIdAtIndex(1, restPath));
            default: return (restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<InstanceClassAction> actions() {
        return Arrays.stream(InstanceClassAction.values());
    }
}
