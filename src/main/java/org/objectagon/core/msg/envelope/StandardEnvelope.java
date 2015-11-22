package org.objectagon.core.msg.envelope;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.VolatileAddressValue;

/**
 * Created by christian on 2015-10-07.
 */
public class StandardEnvelope implements Envelope {
    private final Address target;
    private final Message message;

    public StandardEnvelope(Address target, Message message) {
        this.target = target;
        this.message = message;
    }

    public void unwrap(Unwrapper unwrapper) {
        unwrapper.message(target, message);
    }

    @Override
    public void Targets(Targets targets) {
        targets.target(target).orElseThrow(()-> new SevereError(ErrorClass.ENVELOPE, ErrorKind.UNKNOWN_TARGET, new VolatileAddressValue(StandardField.ADDRESS,target)));
    }
}
