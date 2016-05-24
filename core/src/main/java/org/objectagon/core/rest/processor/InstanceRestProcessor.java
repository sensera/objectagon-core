package org.objectagon.core.rest.processor;

import org.objectagon.core.object.*;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-05-03.
 */
public class InstanceRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        //Field
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    Field.FieldIdentity address = request.getPathItem(3).get().address(Field.FIELD_IDENTITY);
                    return send.setValue(address, request.getValue(FieldValue.VALUE).value());
                }))
        ).add("instance").addIdentity("instanceId").add("field").addIdentity("fieldId").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    Field.FieldIdentity address = request.getPathItem(3).get().address(Field.FIELD_IDENTITY);
                    return send.getValue(address);
                }))
        ).add("instance").addIdentity("instanceId").add("field").addIdentity("fieldId").setOperation(Operation.Get);


        //Relation
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    RelationClass.RelationClassIdentity relationClassIdentity = request.getPathItem(3).get().address(RelationClass.RELATION_CLASS_IDENTITY);
                    return send.addRelation(relationClassIdentity, request.getValue(Instance.INSTANCE_IDENTITY).address());
                }))
        ).add("instance").addIdentity("instanceId").add("relation").addIdentity("relationId").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    RelationClass.RelationClassIdentity relationClassIdentity = request.getPathItem(3).get().address(RelationClass.RELATION_CLASS_IDENTITY);
                    Instance.InstanceIdentity instanceIdentity = request.getPathItem(4).get().address(Instance.INSTANCE_IDENTITY);
                    return send.addRelation(relationClassIdentity, instanceIdentity);
                }))
        ).add("instance").addIdentity("instanceId").add("relation").addIdentity("relationId").addIdentity("instanceId").setOperation(Operation.UpdateExecute);

        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    RelationClass.RelationClassIdentity address = request.getPathItem(3).get().address(RelationClass.RELATION_CLASS_IDENTITY);
                    return send.getRelation(address);
                }))
        ).add("instance").addIdentity("instanceId").add("relation").addIdentity("relationId").setOperation(Operation.Get);
    }

    InstanceProtocolSendConsumer instanceProtocolSendConsumer;

    public InstanceRestProcessor(InstanceProtocolSendConsumer instanceProtocolSendConsumer) {
        this.instanceProtocolSendConsumer = instanceProtocolSendConsumer;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        Optional<PathItem> pathItem = request.getPathItem(1);
        if (!pathItem.isPresent())
            throw new RuntimeException("Field ID not present!");
        Instance.InstanceIdentity instanceIdentity = pathItem.get().address(Instance.INSTANCE_IDENTITY);
        return instanceProtocolSendConsumer.consume(testUser.createInstanceProtocolSend(instanceIdentity), testUser, request, response).addSuccessAction(response::reply);
    }

    interface InstanceProtocolSendConsumer {
        Task consume(InstanceProtocol.Send instanceProtocolSend, ServerCore.TestUser testUser, Request request, Response response);
    }


}



