package org.objectagon.core.msg.protocol;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.message.MinimalMessage;
import org.objectagon.core.msg.message.OneValueMessage;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.receiver.AbstractReceiver;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.task.*;
import org.objectagon.core.utils.IdCounter;

import java.util.*;

import static org.objectagon.core.utils.Util.concat;

/**
 * Created by christian on 2015-10-13.
 */
public abstract class AbstractProtocol<P extends Protocol.Send, R extends Protocol.Reply> extends AbstractReceiver<Protocol.ProtocolAddress> implements Protocol<P,R> {
    private IdCounter idCounter = new IdCounter(0);
    private MappedTransporter<AbstractProtocolSend> sessions = new MappedTransporter<>();
    private MappedTransporter<AbstractProtocolSend.SessionTask> sessionTasks = new MappedTransporter<>();

    public AbstractProtocol(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    public void receive(Envelope envelope) {
        try {
            envelope.targets(sessions::find);
        } catch (SevereError severeError) {
            if (!severeError.getErrorKind().equals(ErrorKind.UNKNOWN_TARGET))
                throw severeError;
        }
        envelope.targets(sessionTasks::find);
    }

    protected abstract class AbstractProtocolReply extends AbstractProtocolSession implements Protocol.Reply {
        public AbstractProtocolReply(CreateReplyParam replyParam) {
            super(replyParam.getComposer());
        }

        protected void transport(Envelope envelope) { getReceiverCtrl().transport(envelope);}

        public void replyOk() {
            transport(composer.create(MinimalMessage.minimal(StandardProtocol.MessageName.OK_MESSAGE)));
        }

        public void replyWithParam(Message.Value param) {
            transport(composer.create(OneValueMessage.oneValue(StandardProtocol.MessageName.OK_MESSAGE, param)));
        }

        @Override
        public void replyWithParam(Iterable<Message.Value> params) {
            transport(composer.create(SimpleMessage.simple(StandardProtocol.MessageName.OK_MESSAGE, params)));
        }

        public void reply(Message.MessageName message) {
            transport(composer.create(message));
        }


    }

    protected class MinimalProtocolReply extends AbstractProtocolReply {
        public MinimalProtocolReply(CreateReplyParam replyParam) {
            super(replyParam);
        }
    }

    protected abstract class AbstractProtocolSend extends AbstractProtocolSession implements Protocol.Send, Transporter {
        public AbstractProtocolSend(CreateSendParam sendParam) {
            super(sendParam.getComposer());
            sessions.put(composer.getSenderAddress(), this);
        }

        @Override public void transport(Envelope envelope) {
            //receive(envelope);
        }

        @Override
        public void terminate() {
            sessions.remove(composer.getSenderAddress());
        }
    }

    protected abstract class AbstractProtocolSession implements Protocol.Session {
        final protected Composer composer;

        public AbstractProtocolSession(Composer composer) {
            this.composer = composer;

        }

        @Override
        public void terminate() {}

        public void replyWithError(org.objectagon.core.exception.ErrorKind errorKind) {
            getReceiverCtrl().transport(composer.create(new StandardProtocol.ErrorMessage(StandardProtocol.ErrorSeverity.UNKNOWN, ErrorClass.UNKNOWN, errorKind)));
        }

        protected void send(Message.MessageName message, Message.Value... values) {
            getReceiverCtrl().transport(composer.create(
                    SimpleMessage.simple(message)
                            .setValues(concat(values))
            ));
        }

        protected void send(Message.MessageName message, Iterable<Message.Value> values) {
            getReceiverCtrl().transport(composer.create(
                    SimpleMessage.simple(message)
                            .setValues(values)
            ));
        }

        protected TaskBuilder taskBuilder() {
            return new StandardTaskBuilder(getTaskCtrl());
        }

        protected LocalTaskCtrl getTaskCtrl() {
            return new LocalTaskCtrl();
        }

        protected Task createProtocolTask(Task.TaskName taskName, Address target, SendMessageAction sendMessageAction){
            return new SessionTask(getTaskCtrl(), taskName, sendMessageAction);
        }

        protected Task task(Task.TaskName taskName, SendMessageAction sendMessageAction) {
            SessionTask sessionTask = new SessionTask(getTaskCtrl(), taskName, sendMessageAction);
            sessionTask.initialize(getReceiverCtrl().getServerId(), System.currentTimeMillis(), idCounter.next(), null);
            return sessionTask;
        }

        private class LocalTaskCtrl implements ReceiverCtrl {
            public LocalTaskCtrl() {}

            @Override
            public void transport(Envelope envelope) {
                getReceiverCtrl().transport(envelope);
            }

            @Override
            public <R extends Receiver> R createReceiver(Name name, Initializer initializer) {
                return getReceiverCtrl().createReceiver(name, initializer);
            }

            @Override
            public void registerFactory(Name name, Server.Factory factory) {
                getReceiverCtrl().registerFactory(name, factory);
            }

            @Override
            public Receiver create(ReceiverCtrl receiverCtrl) {
                return getReceiverCtrl().create(receiverCtrl);
            }

            @Override
            public Server.ServerId getServerId() {
                return getReceiverCtrl().getServerId();
            }
        }

        protected class SessionTask extends AbstractTask<Protocol.SenderAddress> implements Send, Transporter {

            private final SendMessageAction sendMessageAction;
            private final Composer taskComposer;

            public SessionTask(LocalTaskCtrl taskCtrl, TaskName name, SendMessageAction sendMessageAction) {
                super(taskCtrl, name);
                this.sendMessageAction = sendMessageAction;
                this.taskComposer = composer.alternateReceiver(this);
            }

            @Override
            public void initialize(Server.ServerId serverId, long timestamp, long id, Initializer<Protocol.SenderAddress> initializer) {
                super.initialize(serverId, timestamp, id, initializer);
                sessionTasks.put(getAddress(), this);
            }

            @Override
            public void send(Message.MessageName message, Message.Value... values) {
                getReceiverCtrl().transport(taskComposer.create(
                        SimpleMessage.simple(message)
                                .setValues(concat(values))
                ));
            }

            @Override
            public void send(Message.MessageName message, Iterable<Message.Value> values) {
                getReceiverCtrl().transport(taskComposer.create(
                        SimpleMessage.simple(message)
                                .setValues(values)
                ));
            }

            @Override
            protected void internalStart() throws UserException {
                this.sendMessageAction.run(this);
            }

            @Override
            protected SenderAddress createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
                return AbstractProtocol.this.getAddress().create(timestamp, id);
            }

            @Override
            public void transport(Envelope envelope) {
                receive(envelope);
            }
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

    private static class MappedTransporter<T extends Transporter> {
        private Map<Address,T> mapped = new HashMap<>();

        public Optional<Transporter> find(Address address) {
            return Optional.ofNullable(mapped.get(address));
        }

        public T put(Address address, T value) {
            return mapped.put(address, value);
        }

        public void remove(Address address) {
            mapped.remove(address);
        }
    }

}

