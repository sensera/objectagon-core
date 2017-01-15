package org.objectagon.core.rest.processor;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-03.
 */
public class EntityRestProcessor extends AbstractRestProcessor {

    enum EntityType { Class, Meta }

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new EntityRestProcessor(EntityType.Class, ((send, testUser, request, response) -> send.create(request.queryAsValues())))
        ).add("class").setOperation(Operation.SaveNew);
        locatorBuilder.patternBuilder(
                new EntityRestProcessor(EntityType.Meta, ((send, testUser, request, response) -> send.create(request.queryAsValues())))
        ).add("meta").setOperation(Operation.SaveNew);
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
            case Meta: return testUser.createMetaEntityServiceProtocol();
            default: throw new RuntimeException("Internal error");
        }
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        Task task = entityServiceProtocolSendConsumer.consume(getEntityServiceProtocol(testUser), testUser, request, response)
                .addSuccessAction(response::reply);
        MessageValueFieldUtil.create(request.queryAsValues())
                .getValueByFieldOption(NamedField.text("alias"))
                .ifPresent(value -> task.addSuccessAction((messageName, values) -> {
                    Message.Value address = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                    testUser.setValue(NamedField.text(value.asText()), address);
                }));
        return task;
    }

    interface EntityServiceProtocolSendConsumer {
        Task consume(EntityServiceProtocol.Send send, ServerCore.TestUser testUser, Request request, Response response);
    }

}



