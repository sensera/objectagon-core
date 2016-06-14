package org.objectagon.core.rest.processor;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.object.Meta;
import org.objectagon.core.object.MetaProtocol;
import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-05-03.
 */
public class MetaRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new MetaRestProcessor((metaProtocolSend, testUser, request, response) -> metaProtocolSend.createMethod()
                        .addSuccessAction(createCatchAndStoreAddressToAlias(testUser, request)))
        ).add("meta").addIdentity("metaId").add("method").setOperation(Operation.SaveNew);
    }

    MetaProtocolSendConsumer metaProtocolSendConsumer;

    public MetaRestProcessor(MetaProtocolSendConsumer metaProtocolSendConsumer) {
        this.metaProtocolSendConsumer = metaProtocolSendConsumer;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        Optional<PathItem> pathItem = request.getPathItem(1);
        if (!pathItem.isPresent())
            throw new RuntimeException("Field ID not present!");
        Meta.MetaIdentity metaIdentity = pathItem.get().address(Meta.META_IDENTITY);
        return metaProtocolSendConsumer.consume(testUser.createMetaProtocolSend(metaIdentity), testUser, request, response).addSuccessAction(response::reply);
    }

    interface MetaProtocolSendConsumer {
        Task consume(MetaProtocol.Send metaProtocolSend, ServerCore.TestUser testUser, Request request, Response response);
    }

    private static Task.SuccessAction createCatchAndStoreAddressToAlias(ServerCore.TestUser testUser, Request request) {
        return (messageName, values) -> {
            MessageValueFieldUtil.create(request.queryAsValues())
                    .getValueByFieldOption(NamedField.text("alias"))
                    .ifPresent(value -> {
                        Message.Value address = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                        testUser.setValue(NamedField.text(value.asText()), address);
                    });
        };
    }

}



