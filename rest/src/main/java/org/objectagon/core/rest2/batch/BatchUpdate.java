package org.objectagon.core.rest2.batch;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.object.*;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.NameValue;
import org.objectagon.core.utils.NameValueMap;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-04-14.
 */
public interface BatchUpdate {

    interface ActionKind {}

    Task createPlan(Consumer<AddBasis> basisConsumer);

    interface Plan<K extends ActionKind> {
        <A extends Action> A create(K actionKind);

        Task execute();
        Optional<BatchUpdate.ActionContext> getActionContext();
    }

    interface Action {
        void execute(TaskBuilder.ParallelBuilder builder, ActionContext actionContext);
        void lookupInContext(BatchUpdate.ActionContext actionContext);
        <A extends Action> A setFindTargetInContext(Function<ActionContext, Optional<Address>> findTargetInContext);
    }

    interface ActionContext {
        Optional<Message.Value> getValue(Name name);
        ActionContext extend(Consumer<NameValueMap.ExtendNameValueMap> extendActionContext);
        ActionContext extendSingle(Function<Iterable<Message.Value>, Optional<NameValue>> extender);
        void updateAddressName(Name name, Address address);
        void setValueFetchedFromContext(Name name, Consumer<Message.Value> value);
        Message.Values asValues();
    }

    interface AddBasis {
        void addMeta(MetaBasis metaBasis);
        void addClass(ClassBasis classBasis);
        void addInstance(InstanceBasis instanceBasis);
        void parseError(UserException parseError);
    }

    interface Basis<N extends Name> {
        Optional<N> getName();
    }

    interface MetaBasis extends Basis<Meta.MetaName> {

        Stream<MethodPart> getMethods();

        interface MethodPart {
            Optional<Method.MethodName> getName();
            Optional<String> getCode();
            Stream<Method.InvokeParam> getInvokeParams();
        }
    }

    interface ClassBasis extends Basis<InstanceClass.InstanceClassName> {

        Stream<FieldPart> getFields();
        Stream<RelationPart> getRelations();

        interface FieldPart {
            Optional<Field.FieldName> getName();
            Optional<Field.FieldType> getType();
        }

        interface RelationPart {
            Optional<RelationClass.RelationName> getName();
            Optional<InstanceClassNameImpl> getTargetInstanceClassIdentity();
            Optional<RelationClass.RelationType> getRelationType();
        }
    }

    interface InstanceBasis extends Basis<Name> {

        Optional<InstanceClass.InstanceClassName> getClassName();
        Optional<Name> getAlias();
        Stream<ValuePart> getValues();
        Stream<RelationPart> getRelations();

        interface ValuePart {
            Optional<Field.FieldName> getName();
            Optional<Message.Value> getValue();
        }

        interface RelationPart {
            Optional<RelationClass.RelationName> getName();
            Stream<Name> getInstances();
        }
    }

    interface Targets {
        Composer.ResolveTarget getInstanceClassServiceAddress();
        Composer.ResolveTarget getMetaServiceAddress();
    }

}
