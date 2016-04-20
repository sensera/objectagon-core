package org.objectagon.core.msg.composer;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.message.MinimalMessage;

import static org.objectagon.core.msg.message.OneValueMessage.oneValue;
import static org.objectagon.core.msg.message.SimpleMessage.simple;

/**
 * Created by christian on 2015-11-01.
 */
public class ResolveTargetComposer implements Composer {

    private Receiver receiver;
    private ResolveTarget target;
    private Message.Values headers;

    public static Composer create(Receiver receiver, ResolveTarget target, Message.Values headers) {
        return new ResolveTargetComposer(receiver, target, headers);
    }

    public ResolveTargetComposer(Receiver receiver, ResolveTarget target, Message.Values headers) {
        if (target == null)
            throw new SevereError(ErrorClass.COMPOSER, ErrorKind.TARGET_IS_NULL);
        if (receiver == null)
            throw new SevereError(ErrorClass.COMPOSER, ErrorKind.SENDER_IS_NULL);
        this.receiver = receiver;
        this.target = target;
        this.headers = headers;
    }

    @Override
    public Envelope create(Message message) {
        return new StandardEnvelope(receiver.getAddress(), target.getAddress(), message, headers);
    }

    @Override
    public Envelope create(Message.MessageName messageName) {
        return new StandardEnvelope(receiver.getAddress(), target.getAddress(), MinimalMessage.minimal(messageName), headers);
    }

    @Override
    public Envelope create(Message.MessageName messageName, Message.Value... values) {
        if (values.length==0) {
            return new StandardEnvelope(receiver.getAddress(), target.getAddress(), MinimalMessage.minimal(messageName), headers);
        }
        if (values.length==1) {
            return new StandardEnvelope(receiver.getAddress(), target.getAddress(), oneValue(messageName, values[0]), headers);
        }
        return new StandardEnvelope(receiver.getAddress(), target.getAddress(), simple(messageName, values), headers);
    }

    @Override
    public Builder builder(Message.MessageName messageName) {
        throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
    }

    @Override
    public Composer alternateReceiver(Receiver receiver) {
        return new ResolveTargetComposer(receiver, target, headers);
    }

    @Override
    public Address getSenderAddress() {
        return receiver.getAddress();
    }

}
