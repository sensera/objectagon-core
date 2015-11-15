package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.VolatileMessageValue;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.protocol.StandardProtocolImpl;

/**
 * Created by christian on 2015-10-13.
 */
public class BasicWorkerImpl implements BasicWorker {
    private boolean handled = false;
    private Receiver.WorkerContext workerContext;

    public boolean isHandled() {return handled;}
    public void setHandled() {handled = true;}
    public Receiver.WorkerContext getWorkerContext() {return workerContext;}

    public BasicWorkerImpl(Receiver.WorkerContext workerContext) {
        this.workerContext = workerContext;
    }

    protected StandardProtocol.StandardSession createStandardProtocolSession() {
        return new StandardProtocolImpl(workerContext.createStandardComposer(), workerContext.getTransporter()).createSession(workerContext.getSender());
    }

    public void replyOk() {
        createStandardProtocolSession().replyOk();
    }

    public void replyWithError(StandardProtocol.ErrorKind errorKind) {
        createStandardProtocolSession().replyWithError(errorKind);
    }

    @Override
    public void replyWithError(StandardProtocol.ErrorMessageProfile errorMessageProfile) {
        createStandardProtocolSession().replyWithError(errorMessageProfile);
    }

    public void replyWithParam(Message.Value param) {
        createStandardProtocolSession().replyWithParam(param);
    }

    public void replyWithParam(Message message) {
        replyWithParam(new VolatileMessageValue(StandardProtocol.FieldName.PARAM,message));
    }

    public boolean messageHasName(Message.MessageName messageName) {
        return workerContext.hasName(messageName);
    }

    public Message.Value getValue(Message.Field field) {
        return workerContext.getValue(field);
    }
}
