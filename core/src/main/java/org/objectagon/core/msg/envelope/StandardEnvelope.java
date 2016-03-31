package org.objectagon.core.msg.envelope;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.VolatileAddressValue;

/**
 * Created by christian on 2015-10-07.
 */
public class StandardEnvelope implements Envelope {
    private final Address sender;
    private final Address target;
    private final Message message;
    private final Message.Values headers;

    public StandardEnvelope(Address sender, Address target, Message message, Message.Values headers) {
        if (target == null)
            throw new SevereError(ErrorClass.ENVELOPE, ErrorKind.TARGET_IS_NULL);
        if (sender == null)
            throw new SevereError(ErrorClass.ENVELOPE, ErrorKind.SENDER_IS_NULL);
        this.sender = sender;
        this.target = target;
        this.message = message;
        this.headers = headers;
    }

    public void unwrap(Unwrapper unwrapper) {
        unwrapper.message(sender, message);
    }

    @Override
    public Message.Values headers() {return headers;}

    @Override
    public void targets(Targets targets) {
        targets.target(this.target).
                orElseThrow(() -> new SevereError(ErrorClass.ENVELOPE, ErrorKind.UNKNOWN_TARGET, VolatileAddressValue.address(this.target))).
                transport(this);
    }

    @Override
    public String toString() {
        return "*"+message+"@"+target+" from "+sender;
    }
}
