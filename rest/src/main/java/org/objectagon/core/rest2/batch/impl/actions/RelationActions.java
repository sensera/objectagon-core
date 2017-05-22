package org.objectagon.core.rest2.batch.impl.actions;

import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.RelationClassProtocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.LocalActionKind;

/**
 * Created by christian on 2017-04-17.
 */
public class RelationActions extends AbstractActions<RelationClassProtocol.Send> {
    public RelationActions() {
        super(RelationClassProtocol.RELATION_CLASS_PROTOCOL);
    }

    @Override
    public <A extends BatchUpdate.Action> A internalFindActionByKind(LocalActionKind actionKind) {
        switch (actionKind) {
            case SET_RELATION_NAME: return (A) createDataAction(data -> session -> session.setName(data.getName()), (SetNameData<RelationClass.RelationName>) null)
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_RELATION_CLASS_ID));

            default:
                return null;
        }
    }

}
