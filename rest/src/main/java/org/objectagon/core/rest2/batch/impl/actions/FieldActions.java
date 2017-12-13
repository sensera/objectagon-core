package org.objectagon.core.rest2.batch.impl.actions;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.LocalActionKind;

/**
 * Created by christian on 2017-04-17.
 */
public class FieldActions extends AbstractActions<FieldProtocol.Send> {
    public FieldActions() {
        super(FieldProtocol.FIELD_PROTOCOL);
    }

    @Override
    public <A extends BatchUpdate.Action> A internalFindActionByKind(LocalActionKind actionKind) {
        switch (actionKind) {
            case SET_FIELD_NAME: return (A) createDataAction(data -> session -> session.setName(data.getName()), (SetNameData<Field.FieldName>) null)
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_FIELD_ID));
            case SET_FIELD_TYPE: return (A) createDataAction(data -> session -> session.setType(data.getValue()), (SetValue<Field.FieldType>) null)
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_FIELD_ID));
            case SET_FIELD_DEFAULT_VALUE: return (A) createDataAction(data -> session -> session.setDefaultValue(data.getValue()), (SetValue<Message.Value>) null)
                    .setFindTargetInContext(createIdentityContextFinder(RESOLVE_FIELD_ID));
            default:
                return null;
        }
    }

}
