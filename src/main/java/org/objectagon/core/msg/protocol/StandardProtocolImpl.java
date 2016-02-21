package org.objectagon.core.msg.protocol;

import org.objectagon.core.Server;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.MinimalMessage;
import org.objectagon.core.msg.message.OneValueMessage;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardWorker;
import org.objectagon.core.msg.receiver.StandardWorkerImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.IdCounter;

import static org.objectagon.core.utils.Util.notImplemented;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardProtocolImpl extends AbstractProtocol<StandardProtocol.StandardSend, StandardProtocol.StandardReply> implements StandardProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(StandardProtocol.STANDARD_PROTOCOL, StandardProtocolImpl::new);
    }

    public StandardProtocolImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public StandardSend createSend(CreateSendParam createSend) {
        return new StandardSendImpl(createSend);
    }

    @Override
    public StandardReply createReply(CreateReplyParam createReply) {
        return new StandardReplyImpl(createReply);
    }

    @Override
    protected ProtocolAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {return new ProtocolAddressImpl(StandardProtocol.STANDARD_PROTOCOL, serverId);}

    protected class StandardSendImpl extends AbstractProtocolSend implements StandardSend {

        private CreateSendParam createSendParam;

        public StandardSendImpl(CreateSendParam createSendParam) {
            super(createSendParam);
            this.createSendParam = createSendParam;
        }

        @Override
        public Task ping(Address target) {
            return createProtocolTask(MessageName.PING_MESSAGE, target, (send) -> send.send(MessageName.PING_MESSAGE));
        }

        @Override
        public Task log(Address target, Iterable<Message.Value> params) {
            return createProtocolTask(MessageName.LOG_MESSAGE, target, (send) -> send.send(MessageName.LOG_MESSAGE, params));
        }


    }

    protected class StandardReplyImpl extends AbstractProtocolReply implements StandardReply {

        public StandardReplyImpl(CreateReplyParam createReplyParam) {
            super(createReplyParam);
        }

        public void replyWithError(final StandardProtocol.ErrorClass errorClass, final StandardProtocol.ErrorKind errorKind) {
            transport(composer.create(new StandardProtocol.ErrorMessage(StandardProtocol.ErrorSeverity.User, errorClass, errorKind)));
        }

        public void replyWithError(StandardProtocol.ErrorKind errorKind) {
            transport(composer.create(new StandardProtocol.ErrorMessage(StandardProtocol.ErrorSeverity.UNKNOWN, org.objectagon.core.exception.ErrorClass.UNKNOWN, errorKind)));
        }

        @Override
        public void replyWithError(StandardProtocol.ErrorMessageProfile errorMessageProfile) {
            transport(composer.create(new StandardProtocol.ErrorMessage(errorMessageProfile)));
        }


    }
}
