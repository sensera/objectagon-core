package org.objectagon.core.rest.processor;

import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-03.
 */
public class EntityRestProcessor extends AbstractRestProcessor {

    enum EntityType { Class }

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new EntityRestProcessor(EntityType.Class, ((send, testUser, request, response) -> send.create(request.queryAsValues())))
        ).add("class").setOperation(Operation.SaveNew);
    }

    EntityType entityType;
    EntityServiceProtocolSendConsumer entityServiceProtocolSendConsumer;

    public EntityRestProcessor(EntityType entityType, EntityServiceProtocolSendConsumer entityServiceProtocolSendConsumer) {
        this.entityType = entityType;
        this.entityServiceProtocolSendConsumer = entityServiceProtocolSendConsumer;
    }

    private EntityServiceProtocol.Send getEntityServiceProtocol(ServerCore.TestUser testUser) {
        switch (entityType) {
            case Class: return testUser.createInstanceClassEntityServiceProtocol();
            default: throw new RuntimeException("Internal error");
        }
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        return entityServiceProtocolSendConsumer.consume(getEntityServiceProtocol(testUser), testUser, request, response)
                .addSuccessAction(response::reply);
    }

    interface EntityServiceProtocolSendConsumer {
        Task consume(EntityServiceProtocol.Send send, ServerCore.TestUser testUser, Request request, Response response);
    }

}



