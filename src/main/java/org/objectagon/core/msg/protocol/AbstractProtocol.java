package org.objectagon.core.msg.protocol;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNumberValue;
import org.objectagon.core.service.ServiceProtocol;

import java.util.Objects;

import static org.objectagon.core.msg.message.VolatileNumberValue.number;
import static org.objectagon.core.utils.Util.concat;

/**
 * Created by christian on 2015-10-13.
 */
public abstract class AbstractProtocol<U extends Protocol.Session> implements Protocol<U> {
    private long sessionIdCounter;
    private ProtocolName name;

    public ProtocolName getName() {return name;}

    public AbstractProtocol(ProtocolName name) {
        this.name = name;
    }

    protected SessionId nextSessionId() {
        long sessionId;
        synchronized (this) {
            sessionId = sessionIdCounter++;
        }
        return new LocalSessionId(sessionId);
    }

    protected abstract class AbstractProtocolSession implements Protocol.Session, Transporter {
        final protected SessionId sessionId;
        final protected Composer composer;
        final private Transporter transporter;
        final protected SessionOwner sessionOwner;

        @Override public SessionId getSessionId() {return sessionId;}
        @Override public void transport(Envelope envelope) {transporter.transport(envelope);}

        public AbstractProtocolSession(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            this.sessionId = sessionId;
            this.composer = composer;
            this.transporter = transporter;
            this.sessionOwner = sessionOwner;
        }

        public void replyWithError(org.objectagon.core.exception.ErrorKind errorKind) {
            transport(composer.create(new StandardProtocol.ErrorMessage(StandardProtocol.ErrorSeverity.Unknown, ErrorClass.UNKNOWN, errorKind)));
        }

        protected void send(Message.MessageName message, Message.Value... values) {
            transport(composer.create(
                    SimpleMessage.simple(message)
                            .setValues(concat(values))
            ));
        }

        protected void send(Message.MessageName message, Iterable<Message.Value> values) {
            transport(composer.create(
                    SimpleMessage.simple(message)
                            .setValues(values)
            ));
        }
    }

    protected static class LocalSessionId implements Protocol.SessionId {
        private final Long sessionId;

        public LocalSessionId(Long sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalSessionId that = (LocalSessionId) o;
            return Objects.equals(sessionId, that.sessionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sessionId);
        }

        @Override
        public Message.Value toValue() {
            return number(StandardField.SESSION_ID, sessionId);
        }
    }


}
