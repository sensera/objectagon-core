package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.batch.impl.actions.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.NameValue;
import org.objectagon.core.utils.NameValueMap;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by christian on 2017-04-14.
 */
public class PlanImpl implements BatchUpdate.Plan<LocalActionKind> {

    private BatchUpdate.ActionContext actionContext;

    private enum PlanTaskName implements Task.TaskName, Message.MessageName {
        EXECUTE_PLAN,
    }

    TaskBuilder taskBuilder;
    BatchUpdate.Targets targets;
    List<Actions.BaseAction> actions = new ArrayList<>();
    List<Actions> createActionsList;

    public <A extends Actions.BaseAction> Optional<A> getActionByName(Name name) {
        return actions.stream()
                .filter(action -> action.filterName(name))
                .map(baseAction -> (A) baseAction)
                .findAny();
    }

    public PlanImpl(TaskBuilder taskBuilder, BatchUpdate.Targets targets) {
        this.taskBuilder = taskBuilder;
        this.targets = targets;
        this.createActionsList = Arrays.asList(
                new MetaActions(targets.getMetaServiceAddress()),
                new ClassActions(targets.getInstanceClassServiceAddress()),
                new MethodActions(),
                new FieldActions(),
                new RelationActions(),
                new InstanceActions()
        );
    }

    @Override
    public Optional<BatchUpdate.ActionContext> getActionContext() {
        return Optional.ofNullable(actionContext);
    }

    @Override
    public <A extends BatchUpdate.Action> A create(LocalActionKind actionKind) {
        return (A) createActionsList.stream()
                .map(a ->  a.findActionByKind(actionKind))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(action -> add( (Actions.BaseAction) action))
                .orElseThrow(() -> new SevereError(ErrorClass.BATCH_UPDATE, ErrorKind.UNKNOWN, MessageValue.text(actionKind.name())));
    }

    @Override
    public Task execute() {
        final TaskBuilder.ParallelBuilder parallel = taskBuilder.parallel(PlanTaskName.EXECUTE_PLAN);
        actionContext = new MyActionContext();
        actions.stream()
                //.peek(action -> System.out.println("PlanImpl.execute action "+action))
                .forEach(action -> {
                    try {
                        action.execute(parallel, actionContext);
                    } catch (Exception e) {
                        System.out.println("PlanImpl.execute failed action="+action);
                        e.printStackTrace();
                    }
                });
        final Task task = parallel.create();
        task.addSuccessAction((messageName, values) -> {
            final List<Actions.BaseAction> unCompletedActions = actions.stream()
                    .filter(baseAction -> !baseAction.isCompleted())
                    .collect(Collectors.toList());
            if (!unCompletedActions.isEmpty()) {
                System.out.println("PlanImpl.execute unCompletedActions.size="+unCompletedActions.size() + " >>>>>>>>>>>>>>>>>>>>>>");
                unCompletedActions.stream().forEach(baseAction -> System.out.println(baseAction.getClass().getName()+" "+baseAction));
                System.out.println("PlanImpl.execute unCompletedActions <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                throw new UserException(ErrorClass.BATCH_UPDATE, ErrorKind.UNEXPECTED);
            }
        });
        return task;
    }

    private <E extends Actions.BaseAction> E add(E action) {
        actions.add(action);
        return action;
    }

    private static class MyActionContext implements BatchUpdate.ActionContext {
        final NameValueMap nameValueMap;
        final Map<Name,Address> updatedNameAndAddressMap;

        public MyActionContext() {
            nameValueMap = NameValueMap.empty();
            updatedNameAndAddressMap = new HashMap<>();
        }

        private MyActionContext(NameValueMap nameValueMap, Map<Name,Address> updatedNameAndAddressMap) {
            this.nameValueMap = nameValueMap;
            this.updatedNameAndAddressMap = updatedNameAndAddressMap;
        }

        @Override
        public Optional<Message.Value> getValue(Name name) {
            return nameValueMap.get(name);
        }

        @Override
        public void setValueFetchedFromContext(Name name, Consumer<Message.Value> value) {
            getValue(name).ifPresent(value);
        }

        @Override
        public BatchUpdate.ActionContext extend(Consumer<NameValueMap.ExtendNameValueMap> extendActionContext) {
            return new MyActionContext(this.nameValueMap.extend(extendActionContext), updatedNameAndAddressMap);
        }

        @Override
        public BatchUpdate.ActionContext extendSingle(Function<Iterable<Message.Value>, Optional<NameValue>> extender) {
            return new MyActionContext(this.nameValueMap.extend(extender), updatedNameAndAddressMap);
        }

        @Override
        public void updateAddressName(Name name, Address address) {
            updatedNameAndAddressMap.put(name, address);
        }

        @Override
        public Message.Values asValues() {
            return () -> updatedNameAndAddressMap.entrySet().stream()
                    .map(nameAddressEntry -> MessageValue.address(NamedField.address(nameAddressEntry.getKey().toString()), nameAddressEntry.getValue()))
                    .collect(Collectors.toList());
        }
    }
}
