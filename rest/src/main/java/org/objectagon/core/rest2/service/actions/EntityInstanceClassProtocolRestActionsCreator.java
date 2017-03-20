package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.rest2.service.map.EntityProtocolRestActionsMap;
import org.objectagon.core.storage.EntityServiceProtocol;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class EntityInstanceClassProtocolRestActionsCreator implements EntityProtocolRestActionsMap<AbstractSessionRestAction.CreateSendMessageAction<EntityServiceProtocol.Send>,EntityProtocolRestActionsMap.EntityAction> {

    public AbstractSessionRestAction.CreateSendMessageAction<EntityServiceProtocol.Send> getAction(RestActionCreator restActionCreator, EntityProtocolRestActionsMap.EntityAction action) {
        switch (action) {
            case CREATE_CLASS: return (identityStore, restPath, params, data) -> session -> catchAlias(identityStore, params, StandardField.ADDRESS, session.create());
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<EntityAction> actions() {
        return Arrays.stream(EntityAction.values());
    }
}
