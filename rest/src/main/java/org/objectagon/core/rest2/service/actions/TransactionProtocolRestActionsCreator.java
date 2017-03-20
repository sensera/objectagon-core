package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.map.TransactionProtocolRestActionsMap;
import org.objectagon.core.storage.TransactionServiceProtocol;
import org.objectagon.core.task.Task;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class TransactionProtocolRestActionsCreator implements TransactionProtocolRestActionsMap<AbstractSessionRestAction.CreateSendMessageAction<TransactionServiceProtocol.Send>,TransactionProtocolRestActionsMap.TransactionAction> {

    public AbstractSessionRestAction.CreateSendMessageAction<TransactionServiceProtocol.Send> getAction(RestActionCreator restActionCreator, TransactionAction action) {
        switch (action) {
            case CREATE_TRANSACTION: return (identityStore, restPath, params, data) -> session -> session.create()
                    .addSuccessAction(captureTransaction(identityStore));
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    private Task.SuccessAction captureTransaction(RestServiceActionLocator.IdentityStore identityStore) {
        return (messageName, values) -> identityStore.updateSessionTransaction(MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress());
    }

    @Override
    public Stream<TransactionAction> actions() {
        return Arrays.stream(TransactionAction.values());
    }
}
