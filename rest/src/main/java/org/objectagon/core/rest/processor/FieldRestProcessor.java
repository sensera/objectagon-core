package org.objectagon.core.rest.processor;

import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-05-03.
 */
public class FieldRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new FieldRestProcessor(((fieldProtocolSend, testUser, request, response) -> fieldProtocolSend.setName(request.getValue(Field.FIELD_NAME).name())))
        ).add("field").addIdentity("fieldId").add("name").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new FieldRestProcessor(((fieldProtocolSend, testUser, request, response) -> fieldProtocolSend.getName()))
        ).add("field").addIdentity("fieldId").add("name").setOperation(Operation.Get);
    }

    FieldProtocolSendConsumer instanceClassProtocolSendConsumer;

    public FieldRestProcessor(FieldProtocolSendConsumer fieldProtocolSendConsumer) {
        this.instanceClassProtocolSendConsumer = fieldProtocolSendConsumer;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        Optional<PathItem> pathItem = request.getPathItem(1);
        if (!pathItem.isPresent())
            throw new RuntimeException("Field ID not present!");
        Field.FieldIdentity fieldIdentity = pathItem.get().address(Field.FIELD_IDENTITY);

        return instanceClassProtocolSendConsumer.consume(testUser.createFieldProtocolSend(fieldIdentity), testUser, request, response).addSuccessAction(response::reply);
    }

    interface FieldProtocolSendConsumer {
        Task consume(FieldProtocol.Send fieldProtocolSend, ServerCore.TestUser testUser, Request request, Response response);
    }

}



