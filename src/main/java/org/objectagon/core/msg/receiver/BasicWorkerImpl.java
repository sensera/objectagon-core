package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.Util;

import java.util.LinkedList;
import java.util.List;

import static org.objectagon.core.msg.message.VolatileMessageValue.messageName;

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

    protected StandardProtocol.StandardSend createStandardProtocolSend(Address target) {
        StandardProtocol standardProtocol = this.workerContext.createReceiver(StandardProtocol.STANDARD_PROTOCOL, null);
        return standardProtocol.createSend(() -> workerContext.createTargetComposer(target)); //getWorkerContext().createReplyToSenderComposer());
    }

    protected StandardProtocol.StandardReply createStandardProtocolReply() {
        return this.workerContext.<StandardProtocol>createReceiver(StandardProtocol.STANDARD_PROTOCOL, null).createReply(workerContext::createReplyToSenderComposer);
    }

    @Override
    public <R extends Receiver> R createReceiver(Name name, Receiver.Initializer initializer) {
        return workerContext.createReceiver(name, initializer);
    }

    public void replyOk() {
        createStandardProtocolReply().replyOk();
        setHandled();
    }

    public void replyWithError(StandardProtocol.ErrorKind errorKind) {
        createStandardProtocolReply().replyWithError(errorKind);
        setHandled();
    }

    public void replyWithError(StandardProtocol.ErrorMessageProfile errorMessageProfile) {
        createStandardProtocolReply().replyWithError(errorMessageProfile);
        setHandled();
    }

    public void replyWithParam(Message.Value param) {
        createStandardProtocolReply().replyWithParam(param);
        setHandled();
    }

    public void replyWithParam(Message.MessageName message) {
        replyWithParam(messageName(StandardProtocol.FieldName.PARAM, message));
    }

    @Override
    public void replyWithParam(Message.MessageName message, Iterable<Message.Value> values) {
        List<Message.Value> newValues = new LinkedList<>();
        newValues.add(messageName(StandardProtocol.FieldName.PARAM, message));
        values.forEach(newValues::add);
        createStandardProtocolReply().replyWithParam(newValues);
        setHandled();
    }

    @Override
    public void replyWithParam(Iterable<Message.Value> values) {
        createStandardProtocolReply().replyWithParam(values);
        setHandled();
    }

    public boolean messageHasName(Message.MessageName messageName) {
        return workerContext.hasName(messageName);
    }

    public Message.Value getValue(Message.Field field) {
        return workerContext.getValue(field);
    }

    @Override
    public Message.MessageName getMessageName() {
        return workerContext.getMessageName();
    }

    @Override
    public Iterable<Message.Value> getValues() {
        return workerContext.getValues();
    }

    public Task.SuccessAction replyWithSelectedFieldFromResult(Message.Field field, ErrorClass onErrorClass) {
        return ((messageName, values) -> {
            replyWithParam(
                    Util.select(values, field::sameField)
                            .orElseThrow(() -> new SevereError(onErrorClass, ErrorKind.INCONSISTENCY)));
            setHandled();
        });
    }

    @Override
    public boolean isError() {
        return getMessageName().equals(StandardProtocol.MessageName.ERROR_MESSAGE);
    }

    public Task.SuccessAction replyOkSuccessAction() {
        return ((messageName, values) -> {
            replyOk();
            setHandled();
        });
    }

}
