package org.objectagon.core.msg.receiver;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.protocol.StandardProtocol;

import static org.objectagon.core.msg.message.SimpleMessage.simple;
import static org.objectagon.core.utils.Util.printValuesToString;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverImpl<A extends Address, W extends BasicWorker> extends AbstractReceiver<A> implements BasicReceiver<A> {

    public BasicReceiverImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    protected TriggerBuilder<W> triggerBuilder(W worker) {
        return new TriggerBuilder<>(worker);
    }

    abstract protected void handle(W worker);

    public void receive(Envelope envelope) {
        //System.out.println("BasicReceiverImpl.receive "+envelope+" for class "+this.getClass().getSimpleName());
        envelope.unwrap((sender, message) -> {
            W worker = createWorker(getBasicWorkerContext(sender, message));
            handle(worker);
            if (!worker.isHandled()) {
                if (worker.messageHasName(StandardProtocol.MessageName.ERROR_MESSAGE)) {
                    System.out.println("BasicReceiverImpl.receive IGNORED ERROR " + worker.getMessageName() + printValuesToString(worker.getValues()));
                } else {
                    worker.replyWithError(ErrorKind.UNKNOWN_MESSAGE);
                }
            }
        });
    }

    protected BasicWorkerContext getBasicWorkerContext(Address sender, Message message) {
        return new BasicWorkerContext(this, sender, message);
    }

    protected abstract W createWorker(WorkerContext workerContext);

    private static class BasicComposer implements Composer {
        private Receiver receiver;
        private Address target;

        public BasicComposer(Receiver receiver, Address target) {
            this.receiver = receiver;
            this.target = target;
        }

        public Envelope create(Message message) {
            return new StandardEnvelope(receiver.getAddress(), target, message);
        }

        public Envelope create(Message.MessageName messageName) {
            return new StandardEnvelope(receiver.getAddress(), target, simple(messageName));
        }

        @Override
        public Envelope create(Message.MessageName messageName, Message.Value... values) {
            return new StandardEnvelope(receiver.getAddress(), target, simple(messageName, values));
        }

        public Builder builder(Message.MessageName messageName) {
            throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
        }

        @Override
        public Composer alternateReceiver(Receiver receiver) {
            return new BasicComposer(receiver, target);
        }

        @Override
        public Address getSenderAddress() {
            return receiver.getAddress();
        }
    }

    private class BasicWorkerContext implements WorkerContext {
        private final Receiver receiver;
        private final Address sender;
        private final Message message;

        public BasicWorkerContext(Receiver receiver, Address sender, Message message) {
            this.receiver = receiver;
            this.sender = sender;
            this.message = message;
        }

        public Composer createReplyToSenderComposer() {
            return new BasicComposer(receiver, sender);
        }

        @Override
        public Composer createTargetComposer(Address target) {
            return new BasicComposer(receiver, target);
        }

        public Transporter getTransporter() {
            return getReceiverCtrl();
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
        public <R extends Receiver> R createReceiver(Name name, Initializer initializer) {
            return getReceiverCtrl().createReceiver(name, initializer);
        }

        @Override
        public <U extends Protocol.Reply> U createReply(Protocol.ProtocolName protocolName) {
            Protocol protocol = getReceiverCtrl().createReceiver(protocolName, null);
            return (U) protocol.createReply(() -> createReplyToSenderComposer());
        }

        @Override
        public <U extends Protocol.Send> U createSend(Protocol.ProtocolName protocolName, Address target) {
            Protocol protocol = getReceiverCtrl().createReceiver(protocolName, null);
            return (U) protocol.createSend(() -> createTargetComposer(target));
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
