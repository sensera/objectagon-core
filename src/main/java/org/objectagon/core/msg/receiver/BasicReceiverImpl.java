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

    abstract protected void handle(W worker);

    public void receive(Envelope envelope) {
        envelope.unwrap(new Envelope.Unwrapper() {
            public void message(final Address sender, final Message message) {
                WorkerContext workerContext = new WorkerContext() {
                    public Composer createStandardComposer() {
                        return new BasicComposer(sender);
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
                };
                W worker = createWorker(workerContext);
                handle(worker);
                if (!worker.isHandled())
                    worker.replyWithError(ErrorKind.UNKNOWN_MESSAGE);
            }
        });
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

        public Builder builder(Message message) {
            return null;   //TODO Implement this
        }
    }
}
