package org.objectagon.core.task;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.message.MessageValue;

/**
 * Created by christian on 2015-11-03.
 */
public class ProtocolTask<S extends Protocol.Send> extends AbstractTask {
    private final Protocol.ProtocolName protocolName;
    private final Composer composer;
    private final SendMessageAction<S> sendMessageAction;
    private S send;

    ProtocolTask(ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Address target, SendMessageAction<S> sendMessageAction) {
        this(taskCtrl, taskName, protocolName, target, sendMessageAction, MessageValue.values().asValues());
    }

    ProtocolTask(ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Address target, SendMessageAction<S> sendMessageAction, Message.Values headers) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.composer = new StandardComposer(this, target, headers);
        this.sendMessageAction = sendMessageAction;
    }

    ProtocolTask(ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, SendMessageAction<S> sendMessageAction) {
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
        Task run = sendMessageAction.run(send);
        run.addFailedAction(this::failed);
        run.addSuccessAction(this::success);
        run.start();
    }

    public interface SendMessageAction<U extends Protocol.Session> {
        Task run(U session);
    }

    @Override
    protected void handle(TaskWorker worker) {
        super.handle(worker);
    }

}
