package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.ProtocolTask;

import java.util.Optional;

/**
 * Created by christian on 2017-04-16.
 */
public class DataPortalTargetProtocolActionImpl<P extends Protocol.Send, D extends Actions.DataPortal>
        extends
            AbstractProtocolAction<P>
        implements
            Actions.SetTargetAction,
            Actions.DataPortalTargetProtocolAction<D> {

    private Actions.DataPortalSend<P,D> protocolSend;
    private D dataPortal;

    public DataPortalTargetProtocolActionImpl(Protocol.ProtocolName protocolName, Actions.DataPortalSend<P,D> protocolSend) {
        super(protocolName, null);
        this.protocolSend = protocolSend;
    }

    @Override
    protected boolean canStart() {
        return super.canStart() && dataPortal != null && dataPortal.canStart();
    }

    @Override public Optional<D> getDataPortal() {return Optional.ofNullable(dataPortal);}

    @Override public <B extends BatchUpdate.Action> B updateDataPortal(D dataPortal) {
        this.dataPortal = dataPortal;
        return (B) this;
    }

    @Override
    public void lookupInContext(BatchUpdate.ActionContext actionContext) {
        super.lookupInContext(actionContext);
        dataPortal.updateFromContext(actionContext);
    }

    @Override public boolean filterName(Name name) {
        if (dataPortal==null)
            return false;
        return dataPortal.filterName(name);
    }

    @Override protected ProtocolTask.SendMessageAction<P> getProtocol() { return protocolSend.protocolSend(dataPortal); }

    @Override
    public String toString() {
        return "DataPortalTargetProtocolActionImpl{" +
                "protocolSend=" + protocolSend +
                ", dataPortal=" + dataPortal +
                "} " + super.toString();
    }
}
