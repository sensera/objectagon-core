package org.objectagon.core.rest2.batch.impl.actions;

import org.objectagon.core.object.MethodProtocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.LocalActionKind;

/**
 * Created by christian on 2017-04-17.
 */
public class MethodActions extends AbstractActions<MethodProtocol.Send> {
    public MethodActions() {
        super(MethodProtocol.METHOD_PROTOCOL);
    }

    @Override
    public <A extends BatchUpdate.Action> A internalFindActionByKind(LocalActionKind actionKind) {
        switch (actionKind) {
            case SET_METHOD_CODE: return (A) createDataAction(data -> session -> session.setCode(data.getValue()), (SetValue<String>) null)
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_METHOD_ID));

            default:
                return null;
        }
    }

}
