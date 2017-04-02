package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.rest2.service.map.InstanceClassProtocolRestActionsMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

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
                    session.getInstanceByAlias(restPath.getNameAtIndex(3, StandardName::name).get());
            case GET_NAME: return (identityStore, restPath, params, data) -> InstanceClassProtocol.Send::getName;
            case SET_NAME: return (identityStore, restPath, params, data) -> session ->
                    session.setName((InstanceClass.InstanceClassName) restPath.getNameAtIndex(3, StandardName::name).get());
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    @Override
    public Stream<InstanceClassAction> actions() {
        return Arrays.stream(InstanceClassAction.values());
    }
}
