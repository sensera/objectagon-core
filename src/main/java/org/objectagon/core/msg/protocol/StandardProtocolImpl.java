package org.objectagon.core.msg.protocol;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.MinimalMessage;
import org.objectagon.core.msg.message.OneValueMessage;
import org.objectagon.core.msg.message.SimpleMessage;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardProtocolImpl extends AbstractProtocol<StandardProtocol.StandardSession> implements StandardProtocol {

    public StandardProtocolImpl() {
        super(STANDARD_PROTOCOL);
    }

    @Override
    public StandardSession createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new StandardSessionImpl(nextSessionId(), transporter, composer, sessionOwner);
    }

    protected class StandardSessionImpl implements StandardSession {
        private final Protocol.SessionId sessionId;
        private final Transporter transporter;
        private final Composer composer;
        private final SessionOwner sessionOwner;

        @Override public SessionId getSessionId() {return sessionId;}

        public StandardSessionImpl(Protocol.SessionId sessionId, Transporter transporter, Composer composer, SessionOwner sessionOwner) {
            this.sessionId = sessionId;
            this.transporter = transporter;
            this.composer = composer;
            this.sessionOwner = sessionOwner;
        }

        public void replyWithError(final ErrorClass errorClass, final ErrorKind errorKind) {
            transporter.transport(composer.create(new ErrorMessage(ErrorSeverity.User, errorClass, errorKind)));
        }

        public void replyWithError(ErrorKind errorKind) {
            transporter.transport(composer.create(new ErrorMessage(ErrorSeverity.Unknown, org.objectagon.core.exception.ErrorClass.UNKNOWN, errorKind)));
        }

        public void replyOk() {
            transporter.transport(composer.create(MinimalMessage.minimal(MessageName.OK_MESSAGE)));
        }

        public void replyWithParam(Message.Value param) {
            transporter.transport(composer.create(OneValueMessage.oneValue(MessageName.OK_MESSAGE, param)));
        }

        @Override
        public void replyWithParam(Iterable<Message.Value> params) {
            transporter.transport(composer.create(SimpleMessage.simple(MessageName.OK_MESSAGE, params)));
        }

        @Override
        public void replyWithError(ErrorMessageProfile errorMessageProfile) {
            transporter.transport(composer.create(new ErrorMessage(errorMessageProfile)));
        }

        public void reply(Message.MessageName message) {
            transporter.transport(composer.create(message));
        }

        @Override
        public void replyWithError(org.objectagon.core.exception.ErrorKind errorKind) {
            transporter.transport(composer.create(ErrorMessage.error(errorKind)));
        }

    }

}
