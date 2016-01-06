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
    private final Composer composer;
    private final SendMessageAction<S> sendMessageAction;
    private S session;

    public StandardTask(TaskCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Address target, SendMessageAction<S> sendMessageAction) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.composer = new StandardComposer(this, target);
        this.sendMessageAction = sendMessageAction;
    }

    public StandardTask(TaskCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, SendMessageAction<S> sendMessageAction) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.composer = composer.alternateReceiver(this);
        this.sendMessageAction = sendMessageAction;
    }

    @Override
    protected void internalStart() {
        session = getReceiverCtrl().createSession(protocolName, composer);
        addSuccessAction((messageName, values) -> session.terminate());
        addFailedAction((errorClass, errorKind, values) -> session.terminate());
        sendMessageAction.run(session);
    }

    public interface SendMessageAction<U extends Protocol.Session> {
        void run(U session);
    }

}
