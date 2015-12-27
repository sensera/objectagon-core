package org.objectagon.core.msg.protocol;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.message.VolatileAddressValue;
import org.objectagon.core.msg.message.VolatileNumberValue;
import org.objectagon.core.msg.receiver.BasicReceiver;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.service.ServiceProtocol;
import org.objectagon.core.task.*;

import java.util.Objects;
import java.util.Optional;

import static org.objectagon.core.msg.message.VolatileNumberValue.number;
import static org.objectagon.core.utils.Util.concat;
import static org.objectagon.core.utils.Util.notImplemented;

/**
 * Created by christian on 2015-10-13.
 */
public abstract class AbstractProtocol<U extends Protocol.Session> implements Protocol<U> {
    private long sessionIdCounter;
    private ProtocolName name;
    private Server.ServerId serverId;

    public ProtocolName getName() {return name;}
    @Override public ProtocolName getAddress() {return name;}

    public AbstractProtocol(ProtocolName name, Server.ServerId serverId) {
        this.name = name;
        this.serverId = serverId;
    }

    protected SessionId nextSessionId() {
        long sessionId;
        synchronized (this) {
            sessionId = sessionIdCounter++;
        }
        return new LocalSessionId(sessionId);
    }

    @Override
    public void receive(Envelope envelope) {
        notImplemented();
    }

    protected abstract class AbstractProtocolSession implements Protocol.Session, Transporter {
        final protected SessionId sessionId;
        final protected Composer composer;
        final private Transporter transporter;
        final protected SessionOwner sessionOwner;

        @Override public SessionId getSessionId() {return sessionId;}
        @Override public SessionId getAddress() {return sessionId;}
        @Override public void transport(Envelope envelope) {transporter.transport(envelope);}

        public AbstractProtocolSession(SessionId sessionId, SessionOwner sessionOwner) {
            this.sessionId = sessionId;
            this.composer = sessionOwner.getComposer();
            this.transporter = sessionOwner.getTransporter();
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

        <S extends Session> S interpretSessionRequest(ProtocolName protocolName) {
            if (!protocolName.equals(name))
                throw new SevereError(ErrorClass.PROTOCOL, ErrorKind.ILLEGAL_PROTOCOL);
            return (S) this;
        }

        protected TaskBuilder taskBuilder(CtrlId ctrlId) {
            return new StandardTaskBuilder(getTaskCtrl(ctrlId));
        }

        protected LocalTaskCtrl getTaskCtrl(CtrlId ctrlId) {
            return new LocalTaskCtrl(ctrlId){
                @Override
                public <U extends Session> U createSession(ProtocolName protocolName, Composer composer) {
                    return interpretSessionRequest(protocolName);
                }
            };
        }

        protected Task task(Task.TaskName taskName, SendMessageAction sendMessageAction) {
            return new SessionTask(getTaskCtrl(sessionId), taskName, sendMessageAction);
        }

        @Override
        public void receive(Envelope envelope) {
            notImplemented();
        }

        private abstract class LocalTaskCtrl implements Task.TaskCtrl {
            private final CtrlId ctrlId;
            long idCounter;

            public LocalTaskCtrl(CtrlId ctrlId) {
                this.ctrlId = ctrlId;
                idCounter = 0;
            }

            @Override
            public Server.ServerId getServerId() {
                return serverId;
            }

            @Override
            public CtrlId getCtrlId() {
                return ctrlId;
            }

            @Override
            public <U extends Address> U createNewAddress(Receiver<U> receiver, CreateNewAddressParams params) {
                return (U) StandardAddress.standard(getServerId(), getCtrlId(), idCounter++);
            }

            @Override
            public void transport(Envelope envelope) {
                transporter.transport(envelope);
            }
        }

        protected class SessionTask extends AbstractTask implements Send {

            private final SendMessageAction sendMessageAction;

            public SessionTask(TaskCtrl taskCtrl, TaskName name, SendMessageAction sendMessageAction) {
                super(taskCtrl, name);
                this.sendMessageAction = sendMessageAction;
            }

            @Override
            public void send(Message.MessageName message, Message.Value... values) {
                transport(composer.create(
                        SimpleMessage.simple(message)
                                .setValues(concat(values))
                ));
            }

            @Override
            public void send(Message.MessageName message, Iterable<Message.Value> values) {
                transport(composer.create(
                        SimpleMessage.simple(message)
                                .setValues(values)
                ));
            }

            @Override
            protected void internalStart() throws UserException {
                this.sendMessageAction.run(this);
            }
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

    @FunctionalInterface
    public interface SendMessageAction {
        void run(Send send);
    }

    public interface Send {
        void send(Message.MessageName message, Message.Value... values) ;
        void send(Message.MessageName message, Iterable<Message.Value> values);
    }

}
