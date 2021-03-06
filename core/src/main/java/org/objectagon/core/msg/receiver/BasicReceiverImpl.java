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
import org.objectagon.core.task.StandardTaskBuilder;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.objectagon.core.msg.Message.MESSAGE_HEADER_TRACE_ACTIVE;
import static org.objectagon.core.utils.Util.printValuesToString;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverImpl<A extends Address, W extends BasicWorker> extends AbstractReceiver<A> implements BasicReceiver<A> {

    private Lock lock;

    public BasicReceiverImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    protected TriggerBuilder<W> triggerBuilder(W worker) {
        return new TriggerBuilder<>(worker);
    }

    protected void handle(W worker) {
        if (!worker.isHandled()) {
            if (worker.isError()) {
                worker.ignoreWhenUnhandledError();
            } else {
                worker.replyWithError(new SevereError(ErrorClass.RECEIVER, ErrorKind.UNKNOWN_MESSAGE,
                                                      MessageValue.messageName(worker.getMessageName())));
            }
        }
    }

    public void receive(Envelope envelope) {
        envelope.unwrap((sender, message) -> {
            if (lock != null && !lock.letThrouMessages(message.getName())) {
                getReceiverCtrl().envelopeDelayed(lock.createDelayDetails(envelope));
                return;
            }
            unwrapEnvelope(sender, message, envelope.headers());
        });
    }

    private void unwrapEnvelope(Address sender, Message message, Message.Values headers) {
        W worker = createWorker(getBasicWorkerContext(sender, message, headers));
        handle(worker);
    }

    protected BasicWorkerContext getBasicWorkerContext(Address sender, Message message, Message.Values headers) {
        return new BasicWorkerContext(this, sender, message, headers);
    }

    protected void lock(Worker worker, DelayCause delayCause, Long estimatedDelayTime, Predicate<Message.MessageName> ignoreMessagesWithName) {
        if (lock != null)
            throw new SevereError(ErrorClass.RECEIVER, ErrorKind.UNEXPECTED, MessageValue.text("Already locked"));
        lock = new Lock(worker, delayCause, estimatedDelayTime, ignoreMessagesWithName);

    }

    protected void unlock(Worker worker) {
        final Lock lockTmp = this.lock;
        this.lock = null;
        lockTmp.release();
    }

    protected boolean logLevelCheck(Receiver.WorkerContextLogKind logKind) { return false; }

    protected abstract W createWorker(Receiver.WorkerContext workerContext);

    private static class BasicComposer implements Composer {
        private Receiver receiver;
        private Address target;
        private Message.Values headers;

        public BasicComposer(Receiver receiver, Address target, Message.Values headers) {
            if (target == null)
                throw new SevereError(ErrorClass.COMPOSER, ErrorKind.TARGET_IS_NULL);
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
            if (target == null)
                throw new SevereError(ErrorClass.COMPOSER, ErrorKind.TARGET_IS_NULL);
            if (sender == null)
                throw new SevereError(ErrorClass.COMPOSER, ErrorKind.SENDER_IS_NULL);
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

    protected static class RelayComposer implements Composer {
        private Supplier<Address> sender;
        private Address relay;
        private Name target;
        private Message.Values headers;

        public RelayComposer(Supplier<Address> sender, Address relay, Name target, Message.Values headers) {
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
            return new StandardEnvelope(sender.get(), relay, toRelayMessage(message), headers);
        }

        public Envelope create(Message.MessageName messageName) {
            return new StandardEnvelope(sender.get(), relay, toRelayMessage(SimpleMessage.simple(messageName)), headers);
        }

        @Override
        public Envelope create(Message.MessageName messageName, Message.Value... values) {
            return new StandardEnvelope(sender.get(), relay, toRelayMessage(SimpleMessage.simple(messageName, values)), headers);
        }

        public Builder builder(Message.MessageName messageName) {
            throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
        }

        @Override
        public Composer alternateReceiver(Receiver receiver) {
            return new RelayComposer(() -> receiver.getAddress(), relay, target, headers);
        }

        @Override
        public Address getSenderAddress() {
            return sender.get();
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
            List<Message.Value> newHeaders = headers != null ? Util.iterableToList(headers.values()): new ArrayList<>();
            newHeaders.add(MessageValue.messageName(StandardProtocol.FieldName.ORIGINAL_MESSAGE, message.getName()));
            return new BasicComposer(receiver, sender, () -> newHeaders);
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
                    .map(relayServiceAddress -> new RelayComposer(() -> sender, relayServiceAddress, target, headers))
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
            return headers != null && headers.values() != null ? Util.getValueByField(headers.values(), field) : MessageValue.empty(field);
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
        public <A extends Address, R extends Receiver<A>> R createReceiver(Name name, Configurations... configurations) {
            return getReceiverCtrl().createReceiver(name, configurations);
        }

        @Override
        public <U extends Protocol.Reply> U createReply(Protocol.ProtocolName protocolName) {
            Protocol protocol = getReceiverCtrl().createReceiver(protocolName);
            return (U) protocol.createReply(() -> createReplyToSenderComposer());
        }

        @Override
        public TaskBuilder getTaskBuilder() {
            return getReceiverCtrl().createReceiver(StandardTaskBuilder.STANDARD_TASK_BUILDER);
        }

        @Override
        public void log(WorkerContextLogKind kind, String message, Message.Value[] params) {
            if (logLevelCheck(kind) || trace()) {
                System.out.println("["+this.message+"]"+kind+" "+message +" "+ printValuesToString(params));
            }
        }

        @Override public boolean trace() {
            return "TRACE".equalsIgnoreCase(Util.getValueByField(headers.values(), MESSAGE_HEADER_TRACE_ACTIVE).asText());
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
                if (worker.isError()) {
                    worker.ignoreWhenUnhandledError();
                } else {
                    worker.failed(
                            ErrorClass.RECEIVER,
                            ErrorKind.UNKNOWN_MESSAGE,
                            MessageValue.values(
                                    MessageValue.messageName(worker.getMessageName()),
                                    MessageValue.text(TARGET_CLASS_NAME, BasicReceiverImpl.this.getClass().getSimpleName())
                                               ).asValues().values());
                }
        }
    }

    @FunctionalInterface
    public interface RunWorker<W extends BasicWorker> {
        void run(W worker) throws UserException;
    }

    private class Lock {
        private Worker worker;
        private DelayCause delayCause;
        private Long estimatedDelayTime;
        private Predicate<Message.MessageName> ignoreMessagesWithName;
        private LinkedList<Runnable> envelopeDelayRelease = new LinkedList<>();

        public Lock(Worker worker, DelayCause delayCause, Long estimatedDelayTime, Predicate<Message.MessageName> ignoreMessagesWithName) {
            this.worker = worker;
            this.delayCause = delayCause;
            this.estimatedDelayTime = estimatedDelayTime;
            this.ignoreMessagesWithName = ignoreMessagesWithName;
        }

        public DelayDetails createDelayDetails(Envelope envelope) {
            return new DelayDetails() {
                @Override public Envelope getEnvelope() {return envelope;}
                @Override public Optional<DelayCause> getDelayCause() {return Optional.ofNullable(delayCause);}
                @Override public Optional<Long> getEstimatedDelayTime() {return Optional.ofNullable(estimatedDelayTime);}
                @Override public Envelope createDelayReplyEnvelope(long delay) {
                    return envelope.createReplyComposer().create(SimpleMessage.simple(
                                    StandardProtocol.MessageName.DELAYED_MESSAGE,
                                    MessageValue.number(StandardProtocol.FieldName.DELAY, delay),
                                    MessageValue.name(StandardProtocol.FieldName.DELAY_CAUSE, delayCause)));
                }

                @Override public void release(Runnable runnable) {
                    envelopeDelayRelease.add(runnable);
                }
            };
        }

        public boolean letThrouMessages(Message.MessageName messageName) {
            if (ignoreMessagesWithName==null)
                return false;
            return ignoreMessagesWithName.test(messageName);
        }

        public void release() {
            envelopeDelayRelease.forEach(Runnable::run);
        }
    }


}
