package org.objectagon.core.rest.processor;

import org.objectagon.core.msg.Address;
import org.objectagon.core.rest.RestProcessor;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-05.
 */
public abstract class AbstractRestProcessor<A extends Address> implements RestProcessor {

    public AbstractRestProcessor() {}

    abstract Task createActionTask(ServerCore.TestUser testUser, Request request, Response response);

    @Override
    public void process(ServerCore serverCore, Request request, Response response) {
        System.out.println("AbstractRestProcessor.process");
        ServerCore.TestUser testUser = serverCore.createTestUser(request.getUser());

        createActionTask(testUser, request, response)
                .addFailedAction(response::error)
                .start();
    }
}
