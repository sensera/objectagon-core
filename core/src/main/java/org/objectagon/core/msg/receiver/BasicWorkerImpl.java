package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueMessage;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.message.VolatileMessageValue;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.Util;

import java.util.LinkedList;
import java.util.List;

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

    @Override
    public TaskBuilder getTaskBuilder() {
        return getWorkerContext().getTaskBuilder();
    }


    protected StandardProtocol.StandardSend createStandardProtocolSend(Address target) {
        StandardProtocol standardProtocol = this.workerContext.createReceiver(StandardProtocol.STANDARD_PROTOCOL);
        return standardProtocol.createSend(() -> workerContext.createTargetComposer(target)); //getWorkerContext().createReplyToSenderComposer());
    }

    protected StandardProtocol.StandardReply createStandardProtocolReply() {
        return this.workerContext.<Protocol.ProtocolAddress, StandardProtocol>createReceiver(StandardProtocol.STANDARD_PROTOCOL).createReply(workerContext::createReplyToSenderComposer);
    }

    @Override
    public <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Receiver.Configurations configurations) {
        return workerContext.createReceiver(name, configurations);
    }

    public void replyOk() {
        createStandardProtocolReply().replyWithParam(MessageValue.messageName(StandardProtocol.FieldName.ORIGINAL_MESSAGE ,workerContext.getMessageName()));
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
        replyWithParam(VolatileMessageValue.messageName(StandardProtocol.FieldName.PARAM, message));
    }

    public void forward(Address target, MessageValueMessage message) {
        workerContext.getTransporter().transport(
                getWorkerContext().createForwardComposer(target)
                        .create(SimpleMessage.simple(message.getMessageName(), message.getValues()))
        );
    }

    @Override
    public void replyWithParam(Message.MessageName message, Iterable<Message.Value> values) {
        try {
            List<Message.Value> newValues = new LinkedList<>();
            newValues.add(VolatileMessageValue.messageName(StandardProtocol.FieldName.PARAM, message));
            values.forEach(newValues::add);
            createStandardProtocolReply().replyWithParam(newValues);
            setHandled();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replyWithParam(Iterable<Message.Value> values) {
        createStandardProtocolReply().replyWithParam(values);
        setHandled();
    }

    @Override
    public void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        createStandardProtocolReply().replyWithError(new StandardProtocol.ErrorMessageProfile() {
            @Override
            public StandardProtocol.ErrorSeverity getErrorSeverity() {
                return StandardProtocol.ErrorSeverity.Severe;
            }

            @Override
            public StandardProtocol.ErrorClass getErrorClass() {
                return errorClass;
            }

            @Override
            public StandardProtocol.ErrorKind getErrorKind() {
                return errorKind;
            }

            @Override
            public Iterable<Message.Value> getParams() {
                return values;
            }
        });
        setHandled();
    }

    @Override
    public void success(Message.MessageName messageName, Iterable<Message.Value> values) {
        replyWithParam(messageName, values);
    }

    @Override
    public StandardProtocol.ErrorMessageProfile createErrorMessageProfile(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        return new SevereError(errorClass, errorKind, values);
    }

    @Override
    public StandardProtocol.ErrorMessageProfile createErrorMessageProfile(StandardProtocol.ErrorSeverity errorSeverity, StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        switch (errorSeverity) {
            case Critical: return new SevereError(errorClass, errorKind, values);
            case UNKNOWN: return new SevereError(errorClass, errorKind, values);
            case Severe: return new SevereError(errorClass, errorKind, values);
            case User: return new UserException(errorClass, errorKind, values);
            default: return new SevereError(errorClass, errorKind, values);
        }
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

    @Override
    public void start(Task task) {
        task
                .addFailedAction(this::failed)
                .addSuccessAction(this::success)
                .start();
    }

    public Task.SuccessAction replyOkSuccessAction() {
        return ((messageName, values) -> {
            replyOk();
            setHandled();
        });
    }

}
