package org.objectagon.core.rest2.batch.impl.actions;

import org.objectagon.core.msg.Composer;
import org.objectagon.core.object.MetaProtocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.EntityProtocolActionImpl;
import org.objectagon.core.rest2.batch.impl.LocalActionKind;

/**
 * Created by christian on 2017-04-17.
 */
public class MetaActions extends AbstractActions<MetaProtocol.Send> {
    Composer.ResolveTarget metaServiceTarget;

    public MetaActions(Composer.ResolveTarget metaServiceTarget) {
        super(MetaProtocol.META_PROTOCOL);
        this.metaServiceTarget = metaServiceTarget;
    }

    @Override
    public <A extends BatchUpdate.Action> A internalFindActionByKind(LocalActionKind actionKind) {
        switch (actionKind) {
            case CREATE_META: return (A) new EntityProtocolActionImpl(metaServiceTarget)
                    .addResolver(createIdentifierResolver(RESOLVE_META_CLASS_ID));
            case ADD_META_METHOD: return (A) createAction(MetaProtocol.Send::createMethod)
                    .addResolver(createIdentifierResolver(RESOLVE_METHOD_ID))
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_META_CLASS_ID));

            default:
                return null;
        }
    }

}
