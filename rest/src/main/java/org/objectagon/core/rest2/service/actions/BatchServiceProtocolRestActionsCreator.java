package org.objectagon.core.rest2.service.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.rest2.batch.BatchServiceProtocol;
import org.objectagon.core.rest2.dto.RootDTO;
import org.objectagon.core.rest2.model.Model;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.map.BatchServiceProtocolRestActionsMap;
import org.objectagon.core.task.Task;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.objectagon.core.rest2.batch.BatchServiceProtocol.NAMES_FIELD;

/**
 * Created by christian on 2017-02-22.
 */
public class BatchServiceProtocolRestActionsCreator implements BatchServiceProtocolRestActionsMap<BatchServiceProtocolRestActionsMap.BatchAction> {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public <C extends CreateSendMessageAction<BatchServiceProtocol.Send>> CreateSendMessageAction<BatchServiceProtocol.Send> getAction(AddAction<BatchServiceProtocol.Send> restServiceActionCreator, BatchAction action) {
        switch (action) {
            case BATCH_JOB: return (identityStore, restPath, params, data) -> session -> {
                try {
                    return catchAlias(identityStore, params, StandardField.ADDRESS, session.batchUpdate(createUpdateCommandField(data), true)
                            .addSuccessAction(captureTransaction(identityStore)))
                            .addSuccessAction(captureCreated(identityStore));
                } catch (UserException e) {
                    e.printStackTrace();
                    return throwNotImplementedSevereError(action);
                }
            };
            default: return (identityStore, restPath, params, data) -> session -> throwNotImplementedSevereError(action);
        }
    }

    private Model createUpdateCommandField(String data) throws UserException {
        System.out.println("BatchServiceProtocolRestActionsCreator.createUpdateCommandField >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(data);
        System.out.println("BatchServiceProtocolRestActionsCreator.createUpdateCommandField <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        try {
            return mapper.readValue(data, RootDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UserException(ErrorClass.BATCH_UPDATE, ErrorKind.UNEXPECTED, MessageValue.exception(e));
        }
    }

    private Task.SuccessAction captureTransaction(RestServiceActionLocator.IdentityStore identityStore) {
        return (messageName, values) -> identityStore.updateSessionTransaction(MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress());
    }

    private Task.SuccessAction captureCreated(RestServiceActionLocator.IdentityStore identityStore) {
        return (messageName, values) -> MessageValueFieldUtil.create(values).getValueByFieldOption(NAMES_FIELD)
                .ifPresent(value -> MessageValueFieldUtil.create(value.asValues()).stream()
                        .forEach(nameAddress -> identityStore.updateIdentity(nameAddress.asAddress(), nameAddress.getField().getName().toString())));

    }

    @Override
    public Stream<BatchAction> actions() {
        return Arrays.stream(BatchAction.values());
    }
}
