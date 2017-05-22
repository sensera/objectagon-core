package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.TaskBuilder;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by christian on 2017-04-16.
 */
public abstract class AbstractProtocolAction<P extends Protocol.Send> extends AbstractAction  {

    private Protocol.ProtocolName protocolName;
    protected Composer.ResolveTarget target;
    private Function<BatchUpdate.ActionContext,Optional<Address>> findTargetInContext;
    private Actions.CollectTargetAndName collectTargetAndName;


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
        this.target = () -> target;
    }

    protected abstract ProtocolTask.SendMessageAction<P> getProtocol();

    protected Optional<Name> getName() { return Optional.empty();}

    @Override
    public void lookupInContext(BatchUpdate.ActionContext actionContext) {
        super.lookupInContext(actionContext);
        if (findTargetInContext != null)
            findTargetInContext.apply(actionContext).ifPresent(this::setTarget);
    }

    @Override
    public void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
        final Message.Value targetAddress = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
        if (collectTargetAndName!=null) {
            getName().ifPresent(name -> collectTargetAndName.update(targetAddress.asAddress(), name));
        }
        super.success(messageName, values);
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
