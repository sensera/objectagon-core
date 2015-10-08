package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.envelope.StandardEnvelope;
import org.objectagon.core.msg.message.SimpleMessage;
import org.objectagon.core.msg.receiver.BasicReceiver;

/**
 * Created by christian on 2015-10-06.
 */
public class StandardProtocolImpl implements StandardProtocol {

    private BasicReceiver basicReceiver;
    private Transporter transporter;

    public ProtocolName getName() {
        return STANDARD_PROTOCOL;
    }

    public StandardProtocolImpl(BasicReceiver basicReceiver, Transporter transporter) {
        this.basicReceiver = basicReceiver;
        this.transporter = transporter;
    }

    public StandardSession createSession(Address target) {
        return new StandardSessionImpl(target);
    }

    private class StandardSessionImpl implements StandardSession {
        private Address target;

        public StandardSessionImpl(Address target) {
            this.target = target;
        }

        public void sendErrorTo(final String description, final ErrorKind errorKind) {
            transporter.transport(new StandardEnvelope(target, new ErrorMessage(description, errorKind.name())));
        }
    }

}
