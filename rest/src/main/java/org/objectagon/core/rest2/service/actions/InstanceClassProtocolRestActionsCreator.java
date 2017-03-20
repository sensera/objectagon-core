package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.rest2.service.map.InstanceClassProtocolRestActionsMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class InstanceClassProtocolRestActionsCreator implements InstanceClassProtocolRestActionsMap<AbstractSessionRestAction.CreateSendMessageAction<InstanceClassProtocol.Send>,InstanceClassProtocolRestActionsMap.InstanceClassAction> {

    public AbstractSessionRestAction.CreateSendMessageAction<InstanceClassProtocol.Send> getAction(RestActionCreator restActionCreator, InstanceClassAction action) {
        switch (action) {
            case CREATE_INSTANCE: return (identityStore, restPath, params, data) -> session -> catchAlias(identityStore, params, StandardField.ADDRESS, session.createInstance());
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<InstanceClassAction> actions() {
        return Arrays.stream(InstanceClassAction.values());
    }
}
