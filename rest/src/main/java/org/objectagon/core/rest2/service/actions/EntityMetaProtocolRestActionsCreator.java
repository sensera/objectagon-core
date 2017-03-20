package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.rest2.service.map.EntityProtocolRestActionsMap;
import org.objectagon.core.storage.EntityServiceProtocol;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class EntityMetaProtocolRestActionsCreator implements EntityProtocolRestActionsMap<AbstractSessionRestAction.CreateSendMessageAction<EntityServiceProtocol.Send>,EntityProtocolRestActionsMap.EntityAction> {

    public AbstractSessionRestAction.CreateSendMessageAction<EntityServiceProtocol.Send> getAction(RestActionCreator restActionCreator, EntityAction action) {
        switch (action) {
            case CREATE_META: return (identityStore, restPath, params, data) -> session -> session.create();
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<EntityAction> actions() {
        return Arrays.stream(EntityAction.values());
    }
}
