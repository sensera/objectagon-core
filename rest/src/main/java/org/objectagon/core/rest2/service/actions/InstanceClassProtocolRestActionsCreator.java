package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.object.*;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.map.InstanceClassProtocolRestActionsMap;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.objectagon.core.object.Instance.INSTANCE_IDENTITY;
import static org.objectagon.core.task.TaskFailed.failedWithUserException;

/**
 * Created by christian on 2017-02-22.
 */
public class InstanceClassProtocolRestActionsCreator implements InstanceClassProtocolRestActionsMap<InstanceClassProtocolRestActionsMap.InstanceClassAction> {

    @Override
    public <C extends CreateSendMessageAction<InstanceClassProtocol.Send>> CreateSendMessageAction<InstanceClassProtocol.Send> getAction(AddAction<InstanceClassProtocol.Send> restServiceActionCreator, InstanceClassAction action) {
        switch (action) {
            case CREATE_INSTANCE: return (identityStore, restPath, params, data) -> session -> catchAlias(identityStore, params, StandardField.ADDRESS, session.createInstance());
            case ADD_FIELD: return (identityStore, restPath, params, data) -> session -> catchAlias(identityStore, params, StandardField.ADDRESS, session.addField());
            case ADD_RELATION: return (identityStore, restPath, params, data) -> session ->
                    catchAlias(
                            identityStore,
                            params,
                            StandardField.ADDRESS,
                            session.addRelation(RelationClass.RelationType.ASSOCIATION, (InstanceClass.InstanceClassIdentity) restPath.getIdentityAtIndex(3).get()));
            case ATTACHE_METHOD: return (identityStore, restPath, params, data) -> session ->
                    catchAlias(
                            identityStore,
                            params,
                            StandardField.ADDRESS,
                            session.addMethod(
                                    (Method.MethodIdentity) restPath.getIdentityAtIndex(3).get(),
                                    Collections.emptyList(),
                                    Collections.emptyList()));
            case GET_NAMED_INSTANCE: return (identityStore, restPath, params, data) -> session ->
                    session.getInstanceByAlias(restPath.getNameAtIndex(3, StandardName::name).get())
                    .addSuccessAction(findAliasAndMessageIdentityToUpdateAlias(identityStore, INSTANCE_IDENTITY, restPath.valueAtIndex(3).asText()));
            case NAME_INSTANCE: return (identityStore, restPath, params, data) -> session ->
                    session.addInstanceAlias(
                            (Instance.InstanceIdentity) restPath.getIdentityAtIndex(3).get(),
                            restPath.getNameAtIndex(5, StandardName::name).get());
            case GET_NAME: return (identityStore, restPath, params, data) -> InstanceClassProtocol.Send::getName;
            case SET_NAME: return (identityStore, restPath, params, data) -> session ->
                    session.setName((InstanceClass.InstanceClassName) restPath.getNameAtIndex(3, InstanceClassNameImpl::create).get());
            case SET_NAME_PARAM: return (identityStore, restPath, params, data) -> session -> {
                final KeyValueUtil<ParamName, Message.Value> paramNameValueKeyValueUtil = KeyValueUtil.create(params);
                try {
                    final Message.Value wantedName = paramNameValueKeyValueUtil.get(SET_NAME_PARAM).orElseThrow(() -> new UserException(ErrorClass.REST_SERVICE, ErrorKind.NAME_MISSING, MessageValue.name(SET_NAME_PARAM)));
                    return session.setName(InstanceClassNameImpl.create(wantedName.asText()));
                } catch (UserException e) {
                    return failedWithUserException(e);
                }
            };
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<InstanceClassAction> actions() {
        return Arrays.stream(InstanceClassAction.values());
    }
}
