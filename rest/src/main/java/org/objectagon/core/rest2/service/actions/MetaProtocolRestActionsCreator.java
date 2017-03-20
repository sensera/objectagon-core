package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.object.MetaProtocol;
import org.objectagon.core.rest2.service.map.MetaProtocolRestActionsMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class MetaProtocolRestActionsCreator implements MetaProtocolRestActionsMap<AbstractSessionRestAction.CreateSendMessageAction<MetaProtocol.Send>,MetaProtocolRestActionsMap.MetaAction> {

    public AbstractSessionRestAction.CreateSendMessageAction<MetaProtocol.Send> getAction(RestActionCreator restActionCreator, MetaAction action) {
        switch (action) {
            case CREATE_METHOD: return (identityStore, restPath, params, data) -> session -> session.createMethod();
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<MetaAction> actions() {
        return Arrays.stream(MetaAction.values());
    }
}
