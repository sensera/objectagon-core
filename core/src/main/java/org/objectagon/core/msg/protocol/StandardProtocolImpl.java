package org.objectagon.core.msg.protocol;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.SingletonFactory;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardProtocolImpl extends AbstractProtocol<StandardProtocol.StandardSend, StandardProtocol.StandardReply> implements StandardProtocol {

    public static void registerAtServer(Server server) {
        server.registerFactory(StandardProtocol.STANDARD_PROTOCOL, new SingletonFactory<>(StandardProtocolImpl::new));
    }

    public StandardProtocolImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, STANDARD_PROTOCOL);
        createSend = StandardSendImpl::new;
    }

    @Override
    public StandardReply createReply(Protocol.CreateReplyParam createReply) {
        return new StandardReplyImpl(createReply);
    }

    protected class StandardSendImpl extends AbstractProtocolSend implements StandardSend {

        private Protocol.CreateSendParam createSendParam;

        public StandardSendImpl(Protocol.CreateSendParam createSendParam) {
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

        public StandardReplyImpl(Protocol.CreateReplyParam createReplyParam) {
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
