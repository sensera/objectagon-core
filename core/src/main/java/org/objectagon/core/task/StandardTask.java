package org.objectagon.core.task;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
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

    public StandardTask(Receiver.ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Address target, SendMessageAction<S> sendMessageAction) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.composer = new StandardComposer(this, target, MessageValue.values().asValues());
        this.sendMessageAction = sendMessageAction;
    }

    public StandardTask(Receiver.ReceiverCtrl taskCtrl, TaskName taskName, Protocol.ProtocolName protocolName, Composer composer, SendMessageAction<S> sendMessageAction) {
        super(taskCtrl, taskName);
        this.protocolName = protocolName;
        this.composer = composer.alternateReceiver(this);
        this.sendMessageAction = sendMessageAction;
    }

    @Override
    protected void internalStart() {
        Protocol protocol = getReceiverCtrl().createReceiver(protocolName, null);
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

    @Override
    protected Address createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer initializer) {
        return StandardAddress.standard(serverId, timestamp, id);
    }
}
