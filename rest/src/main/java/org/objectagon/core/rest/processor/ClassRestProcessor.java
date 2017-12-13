package org.objectagon.core.rest.processor;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.*;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by christian on 2016-05-03.
 */
public class ClassRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.setName(request.getValue(InstanceClass.INSTANCE_CLASS_NAME).name())))
        ).add("class").addIdentity("classId").add("name").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.getName()))
        ).add("class").addIdentity("classId").add("name").setOperation(Operation.Get);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.addField()
                        .addSuccessAction(createCatchAndStoreAddressToAlias(testUser,request))))
        ).add("class").addIdentity("classId").add("field").setOperation(Operation.SaveNew);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.addRelation(
                        RelationClass.RelationType.valueOf(request.getValue(RelationClass.RELATION_TYPE).text()),
                        testUser.getValue(InstanceClass.INSTANCE_CLASS_IDENTITY).get().asAddress()
                ).addSuccessAction(createCatchAndStoreAddressToAlias(testUser,request))))
        ).add("class").addIdentity("classId").add("relation").setOperation(Operation.UpdateExecute);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> {
                    RelationClass.RelationType type = request.getValueOptional(RelationClass.RELATION_TYPE)
                            .map(requestValue -> RelationClass.RelationType.valueOf(requestValue.text()))
                            .orElse(RelationClass.RelationType.ASSOCIATION);
                    InstanceClass.InstanceClassIdentity relatedClass = request.getPathItem(3).get().address(InstanceClass.INSTANCE_CLASS_IDENTITY);
                    return instanceClassProtocolSend.addRelation(type,relatedClass)
                            .addSuccessAction(createCatchAndStoreAddressToAlias(testUser, request));
                }))
        ).add("class").addIdentity("classId").add("relation").addIdentity("classId").setOperation(Operation.SaveNew);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> instanceClassProtocolSend.createInstance()
                        .addSuccessAction(createCatchAndStoreAddressToAlias(testUser,request))))
        ).add("class").addIdentity("classId").add("instance").setOperation(Operation.SaveNew);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> {
                    Instance.InstanceIdentity instanceIdentity = request.getValue(Instance.INSTANCE_IDENTITY).address();
                    Name instanceAlias = request.getPathItem(3).get().name(StandardField.NAME);
                    return instanceClassProtocolSend.addInstanceAlias(instanceIdentity, instanceAlias);
                }))
        ).add("class").addIdentity("classId").add("instance").addName().setOperation(Operation.SaveNew);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> {
                    Name instanceAlias = request.getPathItem(3).get().name(StandardField.NAME);
                    return instanceClassProtocolSend.getInstanceByAlias(instanceAlias);
                }))
        ).add("class").addIdentity("classId").add("instance").addName().setOperation(Operation.UpdateExecute);

        locatorBuilder.patternBuilder(
                new ClassRestProcessor(((instanceClassProtocolSend, testUser, request, response) -> {
                    Method.MethodIdentity methodIdentity = request.getPathItem(3).get().address(Method.METHOD_IDENTITY);
                    final List<KeyValue<Method.ParamName, Field.FieldIdentity>> keyValues = new ArrayList<>();
                    Stream.of(request.queryAsValues()).forEach(value -> {
                        Method.ParamName paramName = ParamNameImpl.create(value.getField().getName().toString());
                        String fieldIdentityAsText = value.asText();
                        System.out.println("ClassRestProcessor.attachToLocator fieldIdentityAsText="+fieldIdentityAsText);
                        Field.FieldIdentity fieldIdentity = fieldIdentityAsText.contains("-")
                                ? request.getValue(NamedField.text(fieldIdentityAsText)).address()
                                : testUser.getValue(NamedField.address(fieldIdentityAsText)).get().asAddress();
                        keyValues.add(KeyValueUtil.createKeyValue(paramName, fieldIdentity));
                    });
                    return instanceClassProtocolSend.addMethod(methodIdentity, keyValues.stream(), Stream.empty());
                }))
        ).add("class").addIdentity("classId").add("method").addIdentity("methodId").setOperation(Operation.SaveNew);
    }

    InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer;

    public ClassRestProcessor(InstanceClassProtocolSendConsumer instanceClassProtocolSendConsumer) {
        this.instanceClassProtocolSendConsumer = instanceClassProtocolSendConsumer;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        Optional<PathItem> pathItem = request.getPathItem(1);
        if (!pathItem.isPresent())
            throw new RuntimeException("Field ID not present!");
        InstanceClass.InstanceClassIdentity instanceClassIdentity = pathItem.get().address(InstanceClass.INSTANCE_CLASS_IDENTITY);
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



