package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.service.map.InstanceProtocolRestActionsMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class InstanceProtocolRestActionsCreator implements InstanceProtocolRestActionsMap<InstanceProtocolRestActionsMap.InstanceAction> {

    @Override
    public <C extends CreateSendMessageAction<InstanceProtocol.Send>> CreateSendMessageAction<InstanceProtocol.Send> getAction(AddAction<InstanceProtocol.Send> restServiceActionCreator, InstanceAction action) {
        switch (action) {
            case GET_VALUE: return (identityStore, restPath, params, data) -> session -> session.getValue( (Field.FieldIdentity) restServiceActionCreator.getIdAtIndex(1, restPath));
            case GET_INSTANCE: return (identityStore, restPath, params, data) -> session -> session.getValue( (Field.FieldIdentity) restServiceActionCreator.getIdAtIndex(1, restPath));
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<InstanceProtocolRestActionsMap.InstanceAction> actions() {
        return Arrays.stream(InstanceProtocolRestActionsMap.InstanceAction.values());
    }
}
