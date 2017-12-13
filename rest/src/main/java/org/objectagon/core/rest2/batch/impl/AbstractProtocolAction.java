package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by christian on 2017-04-16.
 */
public abstract class AbstractProtocolAction<P extends Protocol.Send> extends AbstractAction  {

    protected Protocol.ProtocolName protocolName;
    protected Composer.ResolveTarget target;
    private Function<BatchUpdate.ActionContext,Optional<Address>> findTargetInContext;

    public AbstractProtocolAction(Protocol.ProtocolName protocolName, Composer.ResolveTarget target) {
        this.protocolName = protocolName;
        this.target = target;
    }

    @Override
    protected boolean canStart() {
        return target != null;
    }

    @Override
    protected void intExecute(TaskBuilder.ParallelBuilder builder) {
        if (target==null)
            throw new NullPointerException("target is null!");
        builder.protocol(protocolName,  target, getProtocol()).firstSuccess(this);
    }

    public void setTarget(Address target) {
        if (this.target!=null) {
            if (target != null) {
                if (!this.target.getAddress().equals(target)) {
                    System.out.println("AbstractProtocolAction.setTarget ERROR IGNORED duplicate TARGET ");
                    return; //TODO this prevents an error. But no good fix. There is a name collision.
                            // Remove this row and activate throw exception. And run all BDD tests
                    //throw new RuntimeException("Cannot (" + protocolName + ") change target from " + this.target.getAddress() + " to " + target);
                }
            } else
                throw new NullPointerException("Try to set address to null!");
        }
        this.target = () -> target;
    }

    protected abstract ProtocolTask.SendMessageAction<P> getProtocol();

    protected Optional<Name> getName() { return Optional.empty();}

    @Override
    public void lookupInContext(BatchUpdate.ActionContext actionContext) {
        super.lookupInContext(actionContext);
        //System.out.println("AbstractProtocolAction.lookupInContext "+protocolName+" findTargetInContext="+findTargetInContext);
        if (findTargetInContext != null) {
            findTargetInContext.apply(actionContext).ifPresent(this::setTarget);
        }
    }

    public <A extends BatchUpdate.Action> A setFindTargetInContext(Function<BatchUpdate.ActionContext, Optional<Address>> findTargetInContext) {
        this.findTargetInContext = findTargetInContext;
        return (A) this;
    }

    @Override
    public String toString() {
        return "AbstractProtocolAction{" +
                "protocolName=" + protocolName +
                ", target=" + target +
                "} " + super.toString();
    }

}
