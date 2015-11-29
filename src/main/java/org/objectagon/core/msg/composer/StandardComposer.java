package org.objectagon.core.msg.composer;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.message.MinimalMessage;
import org.objectagon.core.msg.message.OneValueMessage;
import org.objectagon.core.msg.message.SimpleMessage;

import static org.objectagon.core.msg.message.MinimalMessage.minimal;
import static org.objectagon.core.msg.message.OneValueMessage.oneValue;
import static org.objectagon.core.msg.message.SimpleMessage.simple;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardComposer implements Composer {

    private Address target;

    public static Composer create(Address target) {
        return new StandardComposer(target);
    }

    public StandardComposer(Address target) {
        this.target = target;
    }

    @Override
    public Envelope create(Message message) {
        return new StandardEnvelope(target, message);
    }

    @Override
    public Envelope create(Message.MessageName messageName) {
        return new StandardEnvelope(target, minimal(messageName));
    }

    @Override
    public Envelope create(Message.MessageName messageName, Message.Value... values) {
        if (values.length==0) {
            return new StandardEnvelope(target, minimal(messageName));
        }
        if (values.length==1) {
            return new StandardEnvelope(target, oneValue(messageName, values[0]));
        }
        return new StandardEnvelope(target, simple(messageName, values));
    }

    @Override
    public Builder builder(Message.MessageName messageName) {
        throw new SevereError(ErrorClass.MSG, ErrorKind.NOT_IMPLEMENTED);
    }

}
