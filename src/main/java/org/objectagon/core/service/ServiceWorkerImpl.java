package org.objectagon.core.service;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicWorkerImpl;

/**
 * Created by christian on 2015-10-13.
 */
class ServiceWorkerImpl extends BasicWorkerImpl implements Service.ServiceWorker {
    final private ServiceProtocol serviceProtocol;

    public ServiceWorkerImpl(Receiver.WorkerContext workerContext) {
        super(workerContext);
        serviceProtocol = new ServiceProtocolImpl(workerContext.createStandardComposer(), workerContext.getTransporter());
    }

    public ServiceProtocol.Session createServiceProtocolSession() {
        return serviceProtocol.createSession(getWorkerContext().getSender());
    }

    public void success() {
        createStandardProtocolSession().replyOk();
    }

    public void failed(StandardProtocol.ErrorKind errorKind, String description) {
        createStandardProtocolSession().replyWithError(description, errorKind);
    }

    public void replyWithError(ServiceProtocol.ErrorKind errorKind) {
        createServiceProtocolSession().replyWithError(errorKind);
    }
}
