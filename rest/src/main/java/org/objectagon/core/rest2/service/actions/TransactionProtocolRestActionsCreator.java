package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.map.TransactionProtocolRestActionsMap;
import org.objectagon.core.storage.TransactionManagerProtocol;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-02-22.
 */
public class TransactionProtocolRestActionsCreator implements TransactionProtocolRestActionsMap<TransactionProtocolRestActionsMap.TransactionAction> {

    @Override
    public <C extends CreateSendMessageAction<TransactionManagerProtocol.Send>> CreateSendMessageAction<TransactionManagerProtocol.Send> getAction(AddAction<TransactionManagerProtocol.Send> restServiceActionCreator, TransactionAction action) {
        switch (action) {
            case COMMIT_TRANSACTION: return (identityStore, restPath, params, data) -> TransactionManagerProtocol.Send::commit;
            case ROLLBACK_TRANSACTION: return (identityStore, restPath, params, data) -> TransactionManagerProtocol.Send::rollback;
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
    public Stream<TransactionAction> actions() {
        return Arrays.stream(TransactionAction.values());
    }
}
