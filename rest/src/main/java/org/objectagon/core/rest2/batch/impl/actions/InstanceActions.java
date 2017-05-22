package org.objectagon.core.rest2.batch.impl.actions;

import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.LocalActionKind;

/**
 * Created by christian on 2017-04-17.
 */
public class InstanceActions extends AbstractActions<InstanceProtocol.Send> {


    public InstanceActions() {
        super(InstanceProtocol.INSTANCE_PROTOCOL);
    }

    @Override
    public <A extends BatchUpdate.Action> A internalFindActionByKind(LocalActionKind actionKind) {
        switch (actionKind) {
            case ADD_VALUE: return (A) createDataAction(data -> session ->
                    session.setValue(data.getFieldIdentity(), data.getValue()),
                    (AddValueData) null )
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_INSTANCE_ID));
            case ADD_RELATION: return (A) createDataAction(data -> session -> session
                    .addRelation(data.getRelationClassIdentity(), data.getInstanceIdentity()),
                    (AddRelationData) null)
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_INSTANCE_ID));
            default:
                return null;
        }
    }
}
