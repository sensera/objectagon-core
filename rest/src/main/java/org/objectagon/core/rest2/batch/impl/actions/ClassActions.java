package org.objectagon.core.rest2.batch.impl.actions;

import org.objectagon.core.msg.Composer;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.EntityProtocolActionImpl;
import org.objectagon.core.rest2.batch.impl.LocalActionKind;

/**
 * Created by christian on 2017-04-17.
 */
public class ClassActions extends AbstractActions<InstanceClassProtocol.Send> {

    Composer.ResolveTarget instanceClassServiceTarget;

    public ClassActions(Composer.ResolveTarget instanceClassServiceTarget) {
        super(InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL);
        this.instanceClassServiceTarget = instanceClassServiceTarget;
    }

    @Override
    public <A extends BatchUpdate.Action> A internalFindActionByKind(LocalActionKind actionKind) {
        switch (actionKind) {
            case CREATE_CLASS: return (A) new EntityProtocolActionImpl(instanceClassServiceTarget)
                    .addResolver(createIdentifierResolver(RESOLVE_INSTANCE_CLASS_ID));
            case ADD_CLASS_FIELD: return (A) createAction(InstanceClassProtocol.Send::addField)
                    .addResolver(createIdentifierResolver(RESOLVE_FIELD_ID))
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_INSTANCE_CLASS_ID));
            case ADD_CLASS_RELATION: return (A) createDataAction(data -> session -> session.addRelation(
                    data.getRelationType(),
                    data.getRelatedClass()),
                    (AddClassRelationData) null)
                    .addResolver(createIdentifierResolver(RESOLVE_RELATION_CLASS_ID))
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_INSTANCE_CLASS_ID));
            case ADD_CLASS_METHOD: return (A) createDataAction(data -> session -> session.addMethod(
                    data.getMethodIdentity(),
                    data.getFieldMappings(),
                    data.getDefaultValues()),
                        (AddClassMethodData) null)
                    .addResolver(createIdentifierResolver(RESOLVE_CLASS_METHOD_ID))
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_INSTANCE_CLASS_ID));
            case ADD_ALIAS: return (A) createDataAction(data -> session -> session.addInstanceAlias(
                    data.getInstanceIdentity(),
                    data.getName()),
                    (AddInstanceAliasData) null)
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_INSTANCE_CLASS_ID));
            case CREATE_INSTANCE: return (A) createAction(InstanceClassProtocol.Send::createInstance)
                    .addResolver(createIdentifierResolver(RESOLVE_INSTANCE_ID));
            default:
                return null;
        }
    }

}
