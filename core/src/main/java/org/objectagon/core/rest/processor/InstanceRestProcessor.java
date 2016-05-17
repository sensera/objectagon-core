package org.objectagon.core.rest.processor;

import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-05-03.
 */
public class InstanceRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.setName(request.getValue(InstanceClass.INSTANCE_CLASS_NAME).name())))
        ).add("instance").addIdentity().add("name").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.getName()))
        ).add("instance").addIdentity().add("name").setOperation(Operation.Get);
    }

    InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer;

    public InstanceRestProcessor(InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer) {
        this.instanceClassProtocolSendConsumer = instanceClassProtocolSendConsumer;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        InstanceClass.InstanceClassIdentity instanceClassIdentity = null;
        if (request.getAlias().isPresent()) {
            instanceClassIdentity = (InstanceClass.InstanceClassIdentity) request.getAlias().orElse(null);
        }
        if (instanceClassIdentity==null) {
            Optional<PathItem> pathItem = request.getPathItem(1);
            if (!pathItem.isPresent())
                throw new RuntimeException("Field ID not present!");
            instanceClassIdentity = pathItem.get().address(InstanceClass.INSTANCE_CLASS_IDENTITY);
        }
        return instanceClassProtocolSendConsumer.consume(testUser.createInstanceClassProtocolSend(instanceClassIdentity), testUser, request, response).addSuccessAction(response::reply);
    }

    interface InstanceClassProtocolSendConsumer {
        Task consume(InstanceClassProtocol.Send instanceClassProtocolSend, ServerCore.TestUser testUser, Request request, Response response);
    }


}



