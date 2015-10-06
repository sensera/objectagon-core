package org.objectagon.core.msg.protocol;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.message.BasicMessage;

/**
 * Created by christian on 2015-10-06.
 */
public class BasicProtocolImpl implements BasicProtocol {

    Send send;
    private Address target;

    public BasicProtocolImpl(Send send, Address target) {
        this.send = send;
        this.target = target;
    }

    public void msgFailed() {
        send.send(target, new BasicMessage("FAILED"));
    }

    public String getName() {
        return "BASIC_PROTOCOL";
    }
}
