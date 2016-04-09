package org.objectagon.core.service;

import org.objectagon.core.Server;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-13.
 */
public class ServiceProtocolImpl extends AbstractProtocol<ServiceProtocol.Send, ServiceProtocol.Reply> implements ServiceProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(SERVICE_PROTOCOL, new SingletonFactory<>(ServiceProtocolImpl::new));
    }

    public ServiceProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, SERVICE_PROTOCOL);
        createSend = ServiceProtocolSendImpl::new;
    }

    @Override
    public ServiceProtocol.Reply createReply(CreateReplyParam createReply) {
        return new ServiceProtocolReplyImpl(createReply);
    }

    protected class ServiceProtocolSendImpl extends AbstractProtocolSend implements ServiceProtocol.Send {

        public ServiceProtocolSendImpl(CreateSendParam sendParam) {
            super(sendParam);
        }

        public Task startService() {
            return task(MessageName.START_SERVICE, send -> send.send(MessageName.START_SERVICE));
        }

        public Task stopService() {
            return task(MessageName.STOP_SERVICE, send -> send.send(MessageName.STOP_SERVICE));
        }

    }

    protected class ServiceProtocolReplyImpl extends AbstractProtocolReply implements ServiceProtocol.Reply {

        public ServiceProtocolReplyImpl(CreateReplyParam replyParam) {
            super(replyParam);
        }

        public void replyWithError(ErrorKind errorKind) {
            transport(composer.create(new StandardProtocol.ErrorMessage("", "", errorKind.name())));
        }

    }


}
