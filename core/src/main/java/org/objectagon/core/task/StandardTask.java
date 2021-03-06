package org.objectagon.core.task;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.message.MessageValue;

/**
 * Created by christian on 2015-11-03.
 */
public class StandardTask<S extends Protocol.Send> extends AbstractTask {
    private final Protocol.ProtocolName protocolName;
    private final Composer composer;
    private final SendMessageAction<S> sendMessageAction;
    private S send;

    StandardTask(Receiver.ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Address target, SendMessageAction<S> sendMessageAction) {
        this(taskCtrl, taskName, protocolName, target, sendMessageAction, MessageValue.values().asValues());
    }

    StandardTask(Receiver.ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Address target, SendMessageAction<S> sendMessageAction, Message.Values values) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.composer = new StandardComposer(this, target, values);
        this.sendMessageAction = sendMessageAction;
    }

    StandardTask(Receiver.ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, SendMessageAction<S> sendMessageAction) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.composer = composer.alternateReceiver(this);
        this.sendMessageAction = sendMessageAction;
    }

    @Override
    protected void internalStart() {
        Protocol protocol = getReceiverCtrl().createReceiver(protocolName);
        send = (S) protocol.createSend(() -> composer);
        addSuccessAction((messageName, values) -> send.terminate());
        addFailedAction((errorClass, errorKind, values) -> send.terminate());
        sendMessageAction.run(send);
    }

    public interface SendMessageAction<U extends Protocol.Session> {
        void run(U session);
    }

    @Override
    protected void handle(TaskWorker worker) {
        super.handle(worker);
    }

}
