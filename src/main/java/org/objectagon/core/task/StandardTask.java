package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.receiver.BasicWorker;

/**
 * Created by christian on 2015-11-03.
 */
public class StandardTask<P extends Protocol<S>, S extends Protocol.Session> extends AbstractTask {
    private final P protocol;
    private final Address target;
    private final TaskBuilder.SendMessageAction<S> sendMessageAction;

    public StandardTask(TaskCtrl taskCtrl, TaskName taskName, P protocol, Address target, TaskBuilder.SendMessageAction<S> sendMessageAction) {
        super(taskCtrl, taskName);
        this.protocol = protocol;
        this.target = target;
        this.sendMessageAction = sendMessageAction;
    }

    @Override
    protected void internalStart() {
        sendMessageAction.run(protocol.createSession(target));
    }
}
