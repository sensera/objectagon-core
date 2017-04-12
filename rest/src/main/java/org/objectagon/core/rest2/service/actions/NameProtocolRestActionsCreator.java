package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.rest2.service.map.NameProtocolRestActionsMap;
import org.objectagon.core.service.name.NameServiceProtocol;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class NameProtocolRestActionsCreator implements NameProtocolRestActionsMap<NameProtocolRestActionsMap.NameAction> {

    @Override
    public <C extends CreateSendMessageAction<NameServiceProtocol.Send>> CreateSendMessageAction<NameServiceProtocol.Send> getAction(AddAction<NameServiceProtocol.Send> restServiceActionCreator, NameProtocolRestActionsMap.NameAction action) {
        switch (action) {
            case FIND: return (identityStore, restPath, params, data) -> session ->
                    catchAlias(
                            identityStore,
                            params,
                            StandardField.ADDRESS,
                            session.lookupAddressByName(restPath.getNameAtIndex(1, StandardName::name).get()));
            case UPDATE: return (identityStore, restPath, params, data) -> session ->
                    session.registerName(
                            restPath.getIdentityAtIndex(3).get(),
                            restPath.getNameAtIndex(1, StandardName::name).get()
                    );

            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<NameAction> actions() {
        return Arrays.stream(NameAction.values());
    }
}
