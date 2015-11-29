package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.receiver.BasicWorker;

/**
 * Created by christian on 2015-11-03.
 */
public class StandardTask<S extends Protocol.Session> extends AbstractTask {
    private final Protocol.ProtocolName protocolName;
    private final Address target;
    private final SendMessageAction<S> sendMessageAction;

    public StandardTask(TaskCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Address target, SendMessageAction<S> sendMessageAction) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.target = target;
        this.sendMessageAction = sendMessageAction;
    }

    private Composer createComposer() {
        return new StandardComposer(target);
    }

    @Override
    protected void internalStart() {
        sendMessageAction.run(getReceiverCtrl().createSession(protocolName, createComposer()));
    }

    public interface SendMessageAction<U extends Protocol.Session> {
        void run(U session);
    }

}
