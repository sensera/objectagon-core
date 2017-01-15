package org.objectagon.core.rest.processor;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.rest.RestProcessor;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by christian on 2016-05-05.
 */
public abstract class AbstractNameProtocolRestProcessor<A extends Address> implements RestProcessor {

    int pathItemNameIndex;

    public AbstractNameProtocolRestProcessor() {
        this.pathItemNameIndex = 1;
    }

    abstract Task createResolvedNameTask(A address, ServerCore.TestUser testUser, Request request, Response response);

    @Override
    public void process(ServerCore serverCore, Request request, Response response) {
        System.out.println("AbstractNameProtocolRestProcessor.process");
        ServerCore.TestUser testUser = serverCore.createTestUser(request.getUser());

        Optional<PathItem> pathItem = request.getPathItem(this.pathItemNameIndex);
        if (pathItem==null || !pathItem.isPresent()) {
            response.error(ErrorClass.REST_PROCESSOR, ErrorKind.MISSING_PATH_ITEM, Arrays.asList(MessageValue.number(NamedField.number("NAME_INDEX"), (long) pathItemNameIndex)));
            return;
        }
        Name name = pathItem.get().name(StandardField.NAME);

        testUser.createSearchProtocol()
                .nameSearch(name)
                .addSuccessAction((messageName, values) -> {
                    A address = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
                    createResolvedNameTask(address, testUser, request, response).addFailedAction(response::error).start();
                })
                .addFailedAction(response::error)
                .start();

    }
}
