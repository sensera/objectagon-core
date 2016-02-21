package org.objectagon.core.msg.composer;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;

import static org.objectagon.core.msg.message.MinimalMessage.minimal;
import static org.objectagon.core.msg.message.OneValueMessage.oneValue;
import static org.objectagon.core.msg.message.SimpleMessage.simple;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardComposer implements Composer {

    private Receiver receiver;
    private Address target;

    public static Composer create(Receiver receiver, Address target) {
        return new StandardComposer(receiver, target);
    }

    public StandardComposer(Receiver receiver, Address target) {
        this.receiver = receiver;
        this.target = target;
    }

    @Override
    public Envelope create(Message message) {
        return new StandardEnvelope(receiver.getAddress(), target, message);
    }

    @Override
    public Envelope create(Message.MessageName messageName) {
        return new StandardEnvelope(receiver.getAddress(), target, minimal(messageName));
    }

    @Override
    public Envelope create(Message.MessageName messageName, Message.Value... values) {
        if (values.length==0) {
            return new StandardEnvelope(receiver.getAddress(), target, minimal(messageName));
        }
        if (values.length==1) {
            return new StandardEnvelope(receiver.getAddress(), target, oneValue(messageName, values[0]));
        }
        return new StandardEnvelope(receiver.getAddress(), target, simple(messageName, values));
    }

    @Override
    public Builder builder(Message.MessageName messageName) {
        throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
    }

    @Override
    public Composer alternateReceiver(Receiver receiver) {
        return new StandardComposer(receiver, target);
    }

    @Override
    public Address getSenderAddress() {
        return receiver.getAddress();
    }

}
