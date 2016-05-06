package org.objectagon.core.rest.processor;

import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-03.
 */
public class ClassRestProcessor extends AbstractNameProtocolRestProcessor<InstanceClass.InstanceClassIdentity> {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.addField()))
        ).add("class").addName().add("field").setOperation(Operation.SaveNew);
    }

    InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer;

    public ClassRestProcessor(InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer) {
        this.instanceClassProtocolSendConsumer = instanceClassProtocolSendConsumer;
    }

    @Override
    Task createResolvedNameTask(InstanceClass.InstanceClassIdentity instanceClassIdentity, ServerCore.TestUser testUser, Request request, Response response) {
        System.out.println("ClassRestProcessor.createActionTask instanceClassIdentity="+instanceClassIdentity);
        return instanceClassProtocolSendConsumer.consume(testUser.createInstanceClassProtocolSend(instanceClassIdentity), testUser, request, response)
                .addSuccessAction(response::reply);
    }

    interface InstanceClassProtocolSendConsumer {
        Task consume(InstanceClassProtocol.Send instanceClassProtocolSend, ServerCore.TestUser testUser, Request request, Response response);
    }

}



