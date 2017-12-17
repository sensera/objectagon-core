package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.*;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.map.InstanceProtocolRestActionsMap;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class InstanceProtocolRestActionsCreator implements InstanceProtocolRestActionsMap<InstanceProtocolRestActionsMap.InstanceAction> {

    @Override
    public <C extends CreateSendMessageAction<InstanceProtocol.Send>> CreateSendMessageAction<InstanceProtocol.Send> getAction(AddAction<InstanceProtocol.Send> restServiceActionCreator, InstanceAction action) {
        switch (action) {
            case GET_VALUE: return (identityStore, restPath, params, data) -> session ->
                    session.getValue( (Field.FieldIdentity) restServiceActionCreator.getIdAtIndex(3, restPath));
            case SET_VALUE: return (identityStore, restPath, params, data) -> session ->
                    session.setValue(
                            (Field.FieldIdentity) restServiceActionCreator.getIdAtIndex(3, restPath),
                            MessageValue.text(data));
            case INVOKE_METHOD: return (identityStore, restPath, params, data) -> session -> {
                final List<KeyValue<Method.ParamName, Message.Value>> paramValues = KeyValueUtil.transformKey(params, ParamNameImpl::create);
                return session.invokeMethod(
                        (Method.MethodIdentity) restServiceActionCreator.getIdAtIndex(3, restPath),
                        paramValues);
            };
            case GET_RELATION: return (identityStore, restPath, params, data) -> session ->
                    session.getRelation((RelationClass.RelationClassIdentity) restServiceActionCreator.getIdAtIndex(3, restPath));
            case SET_RELATION:
            case ADD_RELATION: return (identityStore, restPath, params, data) -> session ->
                    session.addRelation(
                            (RelationClass.RelationClassIdentity) restServiceActionCreator.getIdAtIndex(3, restPath),
                            (Instance.InstanceIdentity) restServiceActionCreator.getIdAtIndex(4, restPath));
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    List<KeyValue<Method.ParamName, Message.Value>> createKeyValueParamsFromRestParams(List<KeyValue<ParamName, Message.Value>> params) {
        return params.stream()
                .map(paramNameValueKeyValue -> KeyValueUtil.createKeyValue(
                        ParamNameImpl.create(paramNameValueKeyValue.getKey().toString()),
                        paramNameValueKeyValue.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Stream<InstanceProtocolRestActionsMap.InstanceAction> actions() {
        return Arrays.stream(InstanceProtocolRestActionsMap.InstanceAction.values());
    }
}
