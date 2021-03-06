package org.objectagon.core.service;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;

/**
 * Created by christian on 2015-10-13.
 */
public class ServiceWorkerImpl extends BasicWorkerImpl implements Service.ServiceWorker {

    public ServiceWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
    }

    public void failed(final ErrorClass errorClass, final ErrorKind errorKind, final Iterable<Message.Value> values) {
        replyWithError(createErrorMessageProfile(errorClass, errorKind, values));
    }

    @Override
    public void success(Message.MessageName messageName, Iterable<Message.Value> values) {
        replyWithParam(messageName, values);
    }

    @Override
    public Message.MessageName getMessageName() {
        return getWorkerContext().getMessageName();
    }

    public ServiceProtocol.Reply createServiceProtocolReply() {
        return createReplySession(ServiceProtocol.SERVICE_PROTOCOL);
    }

    public void replyWithError(ServiceProtocol.ErrorKind errorKind) {
        createServiceProtocolReply().replyWithError(errorKind);
    }

    protected  <U extends Protocol.Session> U createReplySession(Protocol.ProtocolName protocolName) {
        return (U) getWorkerContext().createReply(protocolName);
    }

    protected  <U extends Protocol.Session> U createTargetSession(Protocol.ProtocolName protocolName, Address target) {
        return (U) getWorkerContext().createSend(protocolName, target);
    }
}
