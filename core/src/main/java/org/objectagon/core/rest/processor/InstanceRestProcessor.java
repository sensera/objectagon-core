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
                    Field.FieldIdentity address = request.getPathItem(4).get().address(Field.FIELD_IDENTITY);
                    return send.setValue(address, request.getValue(FieldValue.VALUE).value());
                }))
        ).add("instance").addIdentity().add("field").addIdentity().setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    Field.FieldIdentity address = request.getPathItem(4).get().address(Field.FIELD_IDENTITY);
                    return send.getValue(address);
                }))
        ).add("instance").addIdentity().add("field").addIdentity().setOperation(Operation.Get);


        //Relation
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    RelationClass.RelationClassIdentity address = request.getPathItem(4).get().address(RelationClass.RELATION_CLASS_IDENTITY);
                    return send.addRelation(address, request.getValue(Instance.INSTANCE_IDENTITY).address());
                }))
        ).add("instance").addIdentity().add("relation").addIdentity().setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new InstanceRestProcessor(((send, testUser, request, response) -> {
                    RelationClass.RelationClassIdentity address = request.getPathItem(4).get().address(RelationClass.RELATION_CLASS_IDENTITY);
                    return send.getRelation(address);
                }))
        ).add("instance").addIdentity().add("relation").addIdentity().setOperation(Operation.Get);
    }

    InstanceProtocolSendConsumer instanceProtocolSendConsumer;

    public InstanceRestProcessor(InstanceProtocolSendConsumer instanceProtocolSendConsumer) {
        this.instanceProtocolSendConsumer = instanceProtocolSendConsumer;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        Instance.InstanceIdentity instanceIdentity = null;
        if (request.getAlias().isPresent()) {
            instanceIdentity = (Instance.InstanceIdentity) request.getAlias().orElse(null);
        }
        if (instanceIdentity==null) {
            Optional<PathItem> pathItem = request.getPathItem(1);
            if (!pathItem.isPresent())
                throw new RuntimeException("Field ID not present!");
            instanceIdentity = pathItem.get().address(Instance.INSTANCE_IDENTITY);
        }
        return instanceProtocolSendConsumer.consume(testUser.createInstanceProtocolSend(instanceIdentity), testUser, request, response).addSuccessAction(response::reply);
    }

    interface InstanceProtocolSendConsumer {
        Task consume(InstanceProtocol.Send instanceProtocolSend, ServerCore.TestUser testUser, Request request, Response response);
    }


}



