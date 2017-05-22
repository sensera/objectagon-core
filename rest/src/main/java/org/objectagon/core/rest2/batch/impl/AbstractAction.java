package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.NameValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by christian on 2017-04-14.
 */
public abstract class AbstractAction implements Actions.BaseAction, Task.SuccessAction {
    private List<ActionDependency> dependencies = new ArrayList<>();
    private boolean started = false;
    private boolean completed = false;
    private TaskBuilder.ParallelBuilder builder;
    private BatchUpdate.ActionContext actionContext;
    private List<Function<Iterable<Message.Value>,Optional<NameValue>>> resolvers = new ArrayList<>();
    private List<Actions.AddressNameUpdate> addressNameUpdates = new ArrayList<>();

    public <E extends BatchUpdate.Action> E addDependencyAndResolver(E action, Function<Iterable<Message.Value>, Optional<NameValue>> addressResolver) {
        dependencies.add(new ActionDependency(action, addressResolver));
        return action;
    }

    public <E extends Actions.BaseAction> E addAddressNameUpdate(Actions.AddressNameUpdate addressNameUpdate) {
        addressNameUpdates.add(addressNameUpdate);
        return (E) this;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    public <E extends BatchUpdate.Action> E addDependency(E action) {
        return addDependencyAndResolver(action, null);
    }

    protected boolean canStart() { return true; }

    @Override
    public void execute(TaskBuilder.ParallelBuilder builder, BatchUpdate.ActionContext actionContext) {
        this.builder = builder;
        this.actionContext = actionContext;
        lookupInContext(actionContext);
        synchronized (this) {
            if (started || !canStart()) {
                return;
            }
            started = true;
        }
        intExecute(builder);
    }

    public void lookupInContext(BatchUpdate.ActionContext actionContext) {}

    protected abstract void intExecute(TaskBuilder.ParallelBuilder builder);

    protected BatchUpdate.ActionContext extend(Message.MessageName messageName, Iterable<Message.Value> values) {
        List<NameValue> nameValues = new ArrayList<>();
        resolvers.stream()
                .forEach(iterableOptionalFunction -> iterableOptionalFunction.apply(values).ifPresent(nameValues::add));
        if (nameValues.isEmpty())
            return actionContext;
        return actionContext.extend(extendNameValueMap -> nameValues.stream().forEach((nameValue) -> extendNameValueMap.addNamedValue(nameValue.getKey(), nameValue.getValue())));
    }

    @Override
    public void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
        final BatchUpdate.ActionContext extendedActionContext = extend(messageName, values);
        dependencies.stream().forEach(action -> action.execute(builder, extendedActionContext));
        Message.Value address = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
        if (!address.isUnknown())
            addressNameUpdates.stream().forEach(addressNameUpdate -> addressNameUpdate.update(extendedActionContext, address.asAddress()));
        completed = true;
    }

    public <A extends Actions.BaseAction> A addResolver(Function<Iterable<Message.Value>,Optional<NameValue>> resolver) {
        resolvers.add(resolver);
        return (A) this;
    }

    @Override public boolean filterName(Name name) {return false;}

    @Override
    public String toString() {
        return "AbstractAction{" +
                "dependencies=" + dependencies +
                ", started=" + started +
                '}';
    }

    private static class ActionDependency {
        BatchUpdate.Action action;
        Function<Iterable<Message.Value>,Optional<NameValue>> addressResolver;

        public ActionDependency(BatchUpdate.Action action, Function<Iterable<Message.Value>, Optional<NameValue>> addressResolver) {
            this.action = action;
            this.addressResolver = addressResolver;
        }

        public void execute(TaskBuilder.ParallelBuilder builder, BatchUpdate.ActionContext extendedActionContext) {
            if (addressResolver!=null) {
                extendedActionContext = extendedActionContext.extendSingle(addressResolver);
            }
            action.execute(builder, extendedActionContext);
        }
    }
}
