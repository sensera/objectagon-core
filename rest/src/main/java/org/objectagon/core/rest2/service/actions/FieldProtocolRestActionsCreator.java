package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.object.field.FieldNameImpl;
import org.objectagon.core.object.field.StandardFieldType;
import org.objectagon.core.rest2.service.map.FieldProtocolRestActionsMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class FieldProtocolRestActionsCreator implements FieldProtocolRestActionsMap<FieldProtocolRestActionsMap.FieldAction> {

    @Override
    public <C extends CreateSendMessageAction<FieldProtocol.Send>> CreateSendMessageAction<FieldProtocol.Send> getAction(AddAction<FieldProtocol.Send> restServiceActionCreator, FieldAction action) {
        switch (action) {
            case SET_NAME: return (identityStore, restPath, params, data) -> session -> session.setName(restPath.<Field.FieldName>getNameAtIndex(3, FieldNameImpl::create).get());
            case GET_NAME: return (identityStore, restPath, params, data) -> FieldProtocol.Send::getName;
            case SET_DEFAULT_VALUE: return (identityStore, restPath, params, data) -> session -> session.setDefaultValue(restPath.valueAtIndex(3));
            case GET_DEFAULT_VALUE: return (identityStore, restPath, params, data) -> FieldProtocol.Send::getDefaultValue;
            case SET_TYPE: return (identityStore, restPath, params, data) -> session -> session.setType(toFieldTypeFromParamField(restPath.valueAtIndex(3)));
            case GET_TYPE: return (identityStore, restPath, params, data) -> FieldProtocol.Send::getType;
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    Field.FieldType toFieldTypeFromParamField(Message.Value value) {
        if (value.asText().equalsIgnoreCase("number"))
            return StandardFieldType.NUMBER;
        if (value.asText().equalsIgnoreCase("text"))
            return StandardFieldType.TEXT;
        return StandardFieldType.NUMBER;
    }

    @Override
    public Stream<FieldAction> actions() {
        return Arrays.stream(FieldAction.values());
    }
}
