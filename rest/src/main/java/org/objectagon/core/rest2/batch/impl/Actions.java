package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.object.*;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.NameValue;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-04-16.
 */
public interface Actions {

    <A extends BatchUpdate.Action> Optional<A> findActionByKind(LocalActionKind actionKind);

    interface DataPortal {
        void updateFromContext(BatchUpdate.ActionContext actionContext);
        default Consumer<Name> setValueFetchedFromContext(BatchUpdate.ActionContext actionContext, Consumer<Message.Value> updateResultValue) {
            return name -> actionContext.setValueFetchedFromContext(name, updateResultValue);
        }

        default boolean filterName(Name name) { return false; }

        default boolean canStart() { return true; };
    }

    interface BaseAction extends BatchUpdate.Action, DependencyAction {
        boolean filterName(Name name);
        <E extends BaseAction> E addAddressNameUpdate(Actions.AddressNameUpdate addressNameUpdate);
        boolean isCompleted();
    }

    interface EntityProtocolAction extends BaseAction {
        void setName(Name name);
    }

    interface SimpleTargetProtocolAction extends BaseAction, SetTargetAction {
        void setName(Name name);
    }

    interface DataPortalTargetProtocolAction<D extends DataPortal> extends BaseAction, SetTargetAction, DataAction<D> {}

    interface SetTargetAction extends BatchUpdate.Action {
        void setTarget(Address target);
    }

    interface DataAction<D extends DataPortal> extends BatchUpdate.Action {
        Optional<D> getDataPortal();
        <B extends BatchUpdate.Action> B updateDataPortal(D dataPortal);

    }

    interface DependencyAction extends BatchUpdate.Action {
        <E extends BatchUpdate.Action> E addDependency(E action);
        <E extends BatchUpdate.Action> E addDependencyAndResolver(E action, Function<Iterable<Message.Value>, Optional<NameValue>> addressResolver);
    }

    interface AddClassRelationData extends DataPortal {
        RelationClass.RelationType getRelationType();
        InstanceClass.InstanceClassIdentity getRelatedClass();
    }

    interface AddClassMethodData extends DataPortal {
        Method.MethodIdentity getMethodIdentity();
        Stream<KeyValue<Method.ParamName, Field.FieldIdentity>> getFieldMappings();
        Stream<KeyValue<Method.ParamName, Message.Value>> getDefaultValues();
    }

    interface AddRelationData extends DataPortal {
        RelationClass.RelationClassIdentity getRelationClassIdentity();
        Instance.InstanceIdentity getInstanceIdentity();
    }

    interface AddValueData extends DataPortal {
        Field.FieldIdentity getFieldIdentity();
        Message.Value getValue();
    }

    interface AddInstanceAliasData extends DataPortal {
        Instance.InstanceIdentity getInstanceIdentity();
        Name getName();
    }

    interface CreateInstanceData extends DataPortal {
    }

    interface SetNameData<N extends Name> extends DataPortal {
        N getName();
    }

    interface SetValue<V extends Object> extends DataPortal {
        V getValue();
    }

    interface AddMethodParamData extends DataPortal {
        Method.ParamName getParamName();
        Message.Field getField();
        Message.Value getDefaultValue();
    }

    @FunctionalInterface
    interface DataPortalSend<P extends Protocol.Send, D extends DataPortal> {
         ProtocolTask.SendMessageAction<P> protocolSend(D dataPortal);
    }

    interface CollectTargetAndName {
        void update(Address target, Name name);
    }

    @FunctionalInterface
    interface AddressNameUpdate {
        void update(BatchUpdate.ActionContext actionContext, Address address);
    }
}
