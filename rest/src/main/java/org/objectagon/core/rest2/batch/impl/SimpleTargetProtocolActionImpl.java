package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.task.ProtocolTask;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by christian on 2017-04-16.
 */
public class SimpleTargetProtocolActionImpl<P extends Protocol.Send>
        extends
            AbstractProtocolAction<P>
        implements
            Actions.SimpleTargetProtocolAction {

    private ProtocolTask.SendMessageAction<P> protocolSend;
    private Name name;

    @Override public void setName(Name name) { this.name = name; }

    public SimpleTargetProtocolActionImpl(Protocol.ProtocolName protocolName, ProtocolTask.SendMessageAction<P> protocolSend) {
        super(protocolName, null);
        this.protocolSend = protocolSend;
    }

    @Override public void setTarget(Address target) {
        this.target = () -> target;
    }

    @Override protected ProtocolTask.SendMessageAction<P> getProtocol() { return protocolSend; }

    protected Optional<Name> getName() { return Optional.ofNullable(name);}

    @Override public boolean filterName(Name name) {return Objects.equals(this.name, name);}

    @Override
    public String toString() {
        return "SimpleTargetProtocolActionImpl{" +
                "protocolSend=" + protocolSend +
                "} " + super.toString();
    }
}
