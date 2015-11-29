package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.StandardProtocol;

import static org.objectagon.core.msg.message.SimpleMessage.simple;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverImpl<A extends Address, B extends Receiver<A>, C extends BasicReceiverCtrl<B, A>, W extends BasicWorker> implements BasicReceiver<A> {

    private C receiverCtrl;
    private A address;

    public A getAddress() {return address;}
    public C getReceiverCtrl() {return receiverCtrl;}

    public BasicReceiverImpl(C receiverCtrl) {
        this.receiverCtrl = receiverCtrl;
        this.address = receiverCtrl.createNewAddress((B) this);
    }

    protected TriggerBuilder<W> triggerBuilder(W worker) {
        return new TriggerBuilder<>(worker);
    }

    abstract protected void handle(W worker);

    public void receive(Envelope envelope) {
        envelope.unwrap(new Envelope.Unwrapper() {
            public void message(final Address sender, final Message message) {
                W worker = createWorker(getBasicWorkerContext(sender, message));
                handle(worker);
                if (!worker.isHandled())
                    worker.replyWithError(ErrorKind.UNKNOWN_MESSAGE);
            }
        });
    }

    protected BasicWorkerContext getBasicWorkerContext(Address sender, Message message) {
        return new BasicWorkerContext(sender, message);
    }

    protected abstract W createWorker(WorkerContext workerContext);

    private static class BasicComposer implements Composer {
        private Address target;

        public BasicComposer(Address target) {
            this.target = target;
        }

        public Envelope create(Message message) {
            return new StandardEnvelope(target, message);
        }

        public Envelope create(Message.MessageName messageName) {
            return new StandardEnvelope(target, simple(messageName));
        }

        @Override
        public Envelope create(Message.MessageName messageName, Message.Value... values) {
            return new StandardEnvelope(target, simple(messageName, values));
        }

        public Builder builder(Message.MessageName messageName) {
            return null;   //TODO Implement this
        }
    }

    private class BasicWorkerContext implements WorkerContext {
        private final Address sender;
        private final Message message;

        public BasicWorkerContext(Address sender, Message message) {
            this.sender = sender;
            this.message = message;
        }

        public Composer createReplyToSenderComposer() {
            return new BasicComposer(sender);
        }

        @Override
        public Composer createTargetComposer(Address target) {
            return new BasicComposer(target);
        }

        public Transporter getTransporter() {
            return receiverCtrl;
        }

        public Address getSender() {
            return sender;
        }

        public boolean hasName(Message.MessageName messageName) {
            return message.getName().equals(messageName);
        }

        public Message.Value getValue(Message.Field field) {
            return message.getValue(field);
        }

        @Override
        public Message.MessageName getMessageName() {
            return message.getName();
        }

        @Override
        public Iterable<Message.Value> getValues() {
            return message.getValues();
        }

        @Override
        public <U extends Protocol.Session> U createSession(Protocol.ProtocolName protocolName, Composer composer) {
            return getReceiverCtrl().createSession(protocolName, composer);
        }
    }

    protected class TriggerBuilder<W extends BasicWorker> {
        W worker;
        boolean success = false;

        public TriggerBuilder(W worker) {
            this.worker = worker;
        }

        public TriggerBuilder<W> trigger(Message.MessageName messageName, RunWorker<W> runnable) {
            if (!success && messageName.equals(worker.getMessageName())) {
                success = true;
                runnable.run(worker);
            }
            return this;
        }

        public void orElse(RunWorker<W> runnable) {
            if (!success)
                runnable.run(worker);
        }
    }

    @FunctionalInterface
    public interface RunWorker<W extends BasicWorker> {
        void run(W worker);
    }


}
