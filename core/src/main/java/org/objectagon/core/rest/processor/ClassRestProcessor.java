package org.objectagon.core.rest.processor;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.InstanceClassProtocol;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-05-03.
 */
public class ClassRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.setName(request.getValue(InstanceClass.INSTANCE_CLASS_NAME).name())))
        ).add("class").addIdentity().add("name").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.getName()))
        ).add("class").addIdentity().add("name").setOperation(Operation.Get);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.addField()
                        .addSuccessAction(createCatchAndStoreAddressToAlias(testUser,request))))
        ).add("class").addIdentity().add("field").setOperation(Operation.UpdateExecute);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.addRelation(
                        RelationClass.RelationType.valueOf(request.getValue(RelationClass.RELATION_TYPE).text()),
                        testUser.getValue(InstanceClass.INSTANCE_CLASS_IDENTITY).get().asAddress()
                ).addSuccessAction(createCatchAndStoreAddressToAlias(testUser,request))))
        ).add("class").addIdentity().add("relation").setOperation(Operation.UpdateExecute);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.createInstance()
                        .addSuccessAction(createCatchAndStoreAddressToAlias(testUser,request))))
        ).add("class").addIdentity().add("instance").setOperation(Operation.UpdateExecute);
    }

    InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer;

    public ClassRestProcessor(InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer) {
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

    private static Task.SuccessAction createCatchAndStoreAddressToAlias(ServerCore.TestUser testUser, Request request) {
        return (messageName, values) -> {
            MessageValueFieldUtil.create(request.queryAsValues())
                    .getValueByFieldOption(NamedField.text("alias"))
                    .ifPresent(value -> {
                        Message.Value address = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                        System.out.println("ClassRestProcessor.createCatchAndStoreAddressToAlias store alias(" + value.asText() + ") value " + address);
                        testUser.setValue(NamedField.text(value.asText()), address);
                    });
        };
    }

}



