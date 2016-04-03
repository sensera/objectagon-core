package org.objectagon.core.msg.receiver;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.utils.Util;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverImpl<A extends Address, W extends BasicWorker> extends AbstractReceiver<A> implements BasicReceiver<A> {

    public BasicReceiverImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    protected TriggerBuilder<W> triggerBuilder(W worker) {
        return new TriggerBuilder<>(worker);
    }

    abstract protected void handle(W worker);

    public void receive(Envelope envelope) {
        //System.out.println("BasicReceiverImpl.receive "+envelope+" for class "+this.getClass().getSimpleName());
        envelope.unwrap((sender, message) -> {
            W worker = createWorker(getBasicWorkerContext(sender, message, envelope.headers()));
            handle(worker);
            if (!worker.isHandled()) {
                if (worker.messageHasName(StandardProtocol.MessageName.ERROR_MESSAGE)) {
                    System.out.println("BasicReceiverImpl.receive IGNORED ERROR " + worker.getMessageName() + Util.printValuesToString(worker.getValues()));
                } else {
                    worker.replyWithError(ErrorKind.UNKNOWN_MESSAGE);
                }
            }
        });
    }

    protected BasicWorkerContext getBasicWorkerContext(Address sender, Message message, Message.Values headers) {
        return new BasicWorkerContext(this, sender, message, headers);
    }

    protected abstract W createWorker(Receiver.WorkerContext workerContext);

    private static class BasicComposer implements Composer {
        private Receiver receiver;
        private Address target;
        private Message.Values headers;

        public BasicComposer(Receiver receiver, Address target, Message.Values headers) {
            this.receiver = receiver;
            this.target = target;
            this.headers = headers;
        }

        public Envelope create(Message message) {
            return new StandardEnvelope(receiver.getAddress(), target, message, headers);
        }

        public Envelope create(Message.MessageName messageName) {
            return new StandardEnvelope(receiver.getAddress(), target, SimpleMessage.simple(messageName), headers);
        }

        @Override
        public Envelope create(Message.MessageName messageName, Message.Value... values) {
            return new StandardEnvelope(receiver.getAddress(), target, SimpleMessage.simple(messageName, values), headers);
        }

        public Builder builder(Message.MessageName messageName) {
            throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
        }

        @Override
        public Composer alternateReceiver(Receiver receiver) {
            return new BasicComposer(receiver, target, headers);
        }

        @Override
        public Address getSenderAddress() {
            return receiver.getAddress();
        }
    }

    private static class ForwardComposer implements Composer {
        private Address sender;
        private Address target;
        private Message.Values headers;

        public ForwardComposer(Address sender, Address target, Message.Values headers) {
            this.sender = sender;
            this.target = target;
            this.headers = headers;
        }

        public Envelope create(Message message) {
            return new StandardEnvelope(sender, target, message, headers);
        }

        public Envelope create(Message.MessageName messageName) {
            return new StandardEnvelope(sender, target, SimpleMessage.simple(messageName), headers);
        }

        @Override
        public Envelope create(Message.MessageName messageName, Message.Value... values) {
            return new StandardEnvelope(sender, target, SimpleMessage.simple(messageName, values), headers);
        }

        public Builder builder(Message.MessageName messageName) {
            throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
        }

        @Override
        public Composer alternateReceiver(Receiver receiver) {
            return new ForwardComposer(receiver.getAddress(), target, headers);
        }

        @Override
        public Address getSenderAddress() {
            return sender;
        }
    }

    private static class RelayComposer implements Composer {
        private Address sender;
        private Address relay;
        private Name target;
        private Message.Values headers;

        public RelayComposer(Address sender, Address relay, Name target, Message.Values headers) {
            if (sender == null)
                throw new SevereError(ErrorClass.RECEIVER, ErrorKind.SENDER_IS_NULL);
            if (target == null)
                throw new SevereError(ErrorClass.RECEIVER, ErrorKind.TARGET_IS_NULL);
            this.sender = sender;
            this.relay = relay;
            this.target = target;
            this.headers = headers;
        }

        private Message toRelayMessage(Message message) {
            return SimpleMessage.simple(
                    NameServiceProtocol.MessageName.FORWARD,
                    MessageValue.name(StandardField.NAME, target),
                    MessageValue.message(message.getName(), message.getValues()));
        }

        public Envelope create(Message message) {
            return new StandardEnvelope(sender, relay, toRelayMessage(message), headers);
        }

        public Envelope create(Message.MessageName messageName) {
            return new StandardEnvelope(sender, relay, toRelayMessage(SimpleMessage.simple(messageName)), headers);
        }

        @Override
        public Envelope create(Message.MessageName messageName, Message.Value... values) {
            return new StandardEnvelope(sender, relay, toRelayMessage(SimpleMessage.simple(messageName, values)), headers);
        }

        public Builder builder(Message.MessageName messageName) {
            throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
        }

        @Override
        public Composer alternateReceiver(Receiver receiver) {
            return new RelayComposer(receiver.getAddress(), relay, target, headers);
        }

        @Override
        public Address getSenderAddress() {
            return sender;
        }
    }

    private class BasicWorkerContext implements Receiver.WorkerContext {
        private final Receiver receiver;
        private final Address sender;
        private final Message message;
        private final Message.Values headers;

        public BasicWorkerContext(Receiver receiver, Address sender, Message message, Message.Values headers) {
            this.receiver = receiver;
            this.sender = sender;
            this.message = message;
            this.headers = headers;
        }

        public Composer createReplyToSenderComposer() {
            return new BasicComposer(receiver, sender, headers);
        }

        @Override
        public Composer createTargetComposer(Address target) {
            return new BasicComposer(receiver, target, headers);
        }

        @Override
        public Composer createForwardComposer(Address target) { return new ForwardComposer(sender, target, headers); }

        public Composer createRelayComposer(Service.ServiceName relayService, Name target) throws SevereError {
            return getReceiverCtrl()
                    .lookupAddressByAlias(relayService)
                    .map(relayServiceAddress -> new RelayComposer(sender, relayServiceAddress, target, headers))
                    .orElseThrow(() -> new SevereError(ErrorClass.RECEIVER, ErrorKind.NAME_NOT_FOUND, MessageValue.name(relayService)));
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
        public Message.Value getHeader(Message.Field field) {
            return Util.getValueByField(headers.values(), field);
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
        public <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Configurations configurations) {
            return getReceiverCtrl().createReceiver(name, configurations);
        }

        @Override
        public <U extends Protocol.Reply> U createReply(Protocol.ProtocolName protocolName) {
            Protocol protocol = getReceiverCtrl().createReceiver(protocolName);
            return (U) protocol.createReply(() -> createReplyToSenderComposer());
        }

        @Override
        public <U extends Protocol.Send> U createSend(Protocol.ProtocolName protocolName, Address target) {
            Protocol protocol = getReceiverCtrl().createReceiver(protocolName);
            return (U) protocol.createSend(() -> createTargetComposer(target));
        }
    }

    protected class TriggerBuilder<W extends BasicWorker> {
        W worker;

        public TriggerBuilder(W worker) {
            this.worker = worker;
        }

        public TriggerBuilder<W> trigger(Message.MessageName messageName, RunWorker<W> runnable) {
            if (!worker.isHandled() && messageName.equals(worker.getMessageName())) {
                try {
                    runnable.run(worker);
                } catch (UserException e) {
                    worker.replyWithError(e);
                }
                worker.setHandled();
            }
            return this;
        }

        public void orElse(RunWorker<W> runnable) {
            if (!worker.isHandled()) {
                try {
                    runnable.run(worker);
                } catch (UserException e) {
                    worker.replyWithError(e);
                }
                worker.setHandled();
            }
        }

        public void orElseThrowUnhandled() {
            if (!worker.isHandled())
                throw new SevereError(ErrorClass.RECEIVER, ErrorKind.UNKNOWN_MESSAGE, MessageValue.messageName(worker.getMessageName()));
        }
    }

    @FunctionalInterface
    public interface RunWorker<W extends BasicWorker> {
        void run(W worker) throws UserException;
    }


}
