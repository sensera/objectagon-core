package org.objectagon.core.msg.envelope;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.VolatileAddressValue;

import java.util.Optional;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;

/**
 * Created by christian on 2015-10-07.
 */
public class StandardEnvelope implements Envelope {
    private final Address sender;
    private final Address target;
    private final Message message;

    public StandardEnvelope(Address sender, Address target, Message message) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    public void unwrap(Unwrapper unwrapper) {
        unwrapper.message(sender, message);
    }

    @Override
    public void targets(Targets targets) {
        targets.target(this.target).
                orElseThrow(() -> new SevereError(ErrorClass.ENVELOPE, ErrorKind.UNKNOWN_TARGET, address(this.target))).
                transport(this);
    }

    @Override
    public String toString() {
        return "*"+message+"@"+target+" from "+sender;
    }
}
