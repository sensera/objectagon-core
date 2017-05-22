package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.task.ProtocolTask;

/**
 * Created by christian on 2017-04-16.
 */
public class SimpleTargetProtocolActionImpl<P extends Protocol.Send>
        extends
            AbstractProtocolAction<P>
        implements
            Actions.SimpleTargetProtocolAction {

    private ProtocolTask.SendMessageAction<P> protocolSend;

    public SimpleTargetProtocolActionImpl(Protocol.ProtocolName protocolName, ProtocolTask.SendMessageAction<P> protocolSend) {
        super(protocolName, null);
        this.protocolSend = protocolSend;
    }

    @Override public void setTarget(Address target) {
        this.target = () -> target;
    }

    @Override protected ProtocolTask.SendMessageAction<P> getProtocol() { return protocolSend; }


    @Override
    public String toString() {
        return "SimpleTargetProtocolActionImpl{" +
                "protocolSend=" + protocolSend +
                "} " + super.toString();
    }
}
