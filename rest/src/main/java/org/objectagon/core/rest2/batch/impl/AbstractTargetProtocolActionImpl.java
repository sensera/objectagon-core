package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Protocol;

/**
 * Created by christian on 2017-04-16.
 */
public abstract class AbstractTargetProtocolActionImpl<P extends Protocol.Send> extends AbstractProtocolAction<P> implements Actions.SetTargetAction {

    public AbstractTargetProtocolActionImpl(Protocol.ProtocolName protocolName) {
        super(protocolName, null);
    }

    public void setTarget(Address target) {
        this.target = () -> target;
    }

}
