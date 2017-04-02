package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.object.MetaProtocol;
import org.objectagon.core.rest2.service.map.MetaProtocolRestActionsMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class MetaProtocolRestActionsCreator implements MetaProtocolRestActionsMap<MetaProtocolRestActionsMap.MetaAction> {

    @Override
    public <C extends CreateSendMessageAction<MetaProtocol.Send>> CreateSendMessageAction<MetaProtocol.Send> getAction(AddAction<MetaProtocol.Send> restServiceActionCreator, MetaAction action) {
        switch (action) {
            case CREATE_METHOD: return (identityStore, restPath, params, data) -> session ->
                    catchAlias(identityStore, params, StandardField.ADDRESS, session.createMethod());
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<MetaAction> actions() {
        return Arrays.stream(MetaAction.values());
    }
}
