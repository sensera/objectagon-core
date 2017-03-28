package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.rest2.service.map.EntityProtocolRestActionsMap;
import org.objectagon.core.storage.EntityServiceProtocol;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class EntityProtocolRestActionsCreator implements EntityProtocolRestActionsMap<EntityProtocolRestActionsMap.EntityAction> {

    @Override
    public <C extends CreateSendMessageAction<EntityServiceProtocol.Send>> CreateSendMessageAction<EntityServiceProtocol.Send> getAction(AddAction<EntityServiceProtocol.Send> restServiceActionCreator, EntityAction action) {
        switch (action) {
            case CREATE_META: return (identityStore, restPath, params, data) -> session -> catchAlias(identityStore, params, StandardField.ADDRESS, session.create());
            case CREATE_CLASS: return (identityStore, restPath, params, data) -> session -> catchAlias(identityStore, params, StandardField.ADDRESS, session.create());
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<EntityAction> actions() {
        return Arrays.stream(EntityAction.values());
    }
}
