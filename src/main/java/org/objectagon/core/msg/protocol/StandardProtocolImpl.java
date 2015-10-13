package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.message.SimpleMessage;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardProtocolImpl implements StandardProtocol {

    private Composer composer;
    private Transporter transporter;

    public ProtocolName getName() {
        return STANDARD_PROTOCOL;
    }

    public StandardProtocolImpl(Composer composer, Transporter transporter) {
        this.composer = composer;
        this.transporter = transporter;
    }

    public StandardSession createSession(Composer composer, Address target) {
        return new StandardSessionImpl(composer, target);
    }

    public StandardSession createSession(Address target) {
        return new StandardSessionImpl(composer, target);
    }

    protected class StandardSessionImpl implements StandardSession {
        private Composer composer;
        private Address target;

        public StandardSessionImpl(Composer composer, Address target) {
            this.composer = composer;
            this.target = target;
        }

        public void replyWithError(final String description, final ErrorKind errorKind) {
            transporter.transport(composer.create(new ErrorMessage(description, errorKind.name())));
        }

        public void replyWithError(ErrorKind errorKind) {
            transporter.transport(composer.create(new ErrorMessage("", errorKind.name())));
        }

        public void replyOk() {
            transporter.transport(composer.create(new OkMessage()));
        }

        public void reply(Message.MessageName message) {
            transporter.transport(composer.create(message));
        }
    }

}
