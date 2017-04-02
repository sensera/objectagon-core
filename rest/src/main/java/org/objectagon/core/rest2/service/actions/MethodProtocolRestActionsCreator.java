package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.MethodProtocol;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.map.MethodProtocolRestActionsMap;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class MethodProtocolRestActionsCreator implements MethodProtocolRestActionsMap<MethodProtocolRestActionsMap.MethodAction> {

    @Override
    public <C extends CreateSendMessageAction<MethodProtocol.Send>> CreateSendMessageAction<MethodProtocol.Send> getAction(AddAction<MethodProtocol.Send> restServiceActionCreator, MethodAction action) {
        switch (action) {
            case SET_CODE: return (identityStore, restPath, params, data) -> session -> session.setCode(data);
            case GET_CODE: return (identityStore, restPath, params, data) -> MethodProtocol.Send::getCode;
            case CREATE_PARAM: return (identityStore, restPath, params, data) -> session -> {
                final KeyValueUtil<ParamName, Message.Value> paramNameValueKeyValueUtil = KeyValueUtil.create(params);
                final Message.Field field = paramNameValueKeyValueUtil.get(TYPE_METHOD_PARAM).map(this::toMethodParamField).orElse(NamedField.number("NUMBER"));
                final Message.Value defaultValue = paramNameValueKeyValueUtil.get(DEFAULT_VALUE_METHOD_PARAM).orElse(null);
                final Method.ParamName paramName = ParamNameImpl.create(restPath.valueAtIndex(3).asText());
                return session.addParam(paramName, field, defaultValue);
            };
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    Message.Field toMethodParamField(Message.Value value) {
        if (value.asText().equalsIgnoreCase("NUMBER"))
            return NamedField.number("NUMBER");
        if (value.asText().equalsIgnoreCase("TEXT"))
            return NamedField.text("TEXT");
        if (value.asText().equalsIgnoreCase("ADDRESS"))
            return NamedField.address("ADDRESS");
        if (value.asText().equalsIgnoreCase("VALUES"))
            return NamedField.values("VALUES");
        if (value.asText().equalsIgnoreCase("NAME"))
            return NamedField.name("NAME");
        if (value.asText().equalsIgnoreCase("MAP"))
            return NamedField.map("MAP");
        return NamedField.number("NUMBER");
    }

    @Override
    public Stream<MethodAction> actions() {
        return Arrays.stream(MethodAction.values());
    }
}
