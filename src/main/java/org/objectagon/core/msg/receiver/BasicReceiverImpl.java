package org.objectagon.core.msg.receiver;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2015-10-06.
 */
public abstract class BasicReceiverImpl<U extends StandardAddress, E extends BasicReceiverCtrl<U>, W extends BasicWorker> implements BasicReceiver<U> {

    private E receiverCtrl;
    private U address;

    public U getAddress() {return address;}
    public E getReceiverCtrl() {return receiverCtrl;}

    public BasicReceiverImpl(E receiverCtrl) {
        this.receiverCtrl = receiverCtrl;
        this.address = receiverCtrl.createNewAddress(this);
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
                };
                W worker = createWorker(workerContext);
                handle(worker);
                if (!worker.isHandled())
                    worker.replyWithError(StandardProtocol.ErrorKind.UnknownMessage);
            }
        });
    }

    protected W createWorker(WorkerContext workerContext) {
        return (W) new BasicWorkerImpl(workerContext);
    }

    private static class BasicComposer implements Composer {
        private Address target;

        public BasicComposer(Address target) {
            this.target = target;
        }

        public Envelope create(Message message) {
            return new StandardEnvelope(target, message);
        }

        public Envelope create(Message.MessageName messageName) {
            return new StandardEnvelope(target, new SimpleMessage(messageName));
        }

        public Builder builder(Message message) {
            return null;   //TODO Implement this
        }
    }
}
