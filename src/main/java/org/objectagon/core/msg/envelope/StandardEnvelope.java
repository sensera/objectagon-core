package org.objectagon.core.msg.envelope;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Envelope;
import org.objectagon.core.msg.Message;

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
}
