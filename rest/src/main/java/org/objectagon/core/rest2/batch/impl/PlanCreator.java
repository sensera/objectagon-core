package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.NameValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by christian on 2017-04-14.
 */
public class PlanCreator implements BatchUpdate.AddBasis {

    private final PlanImpl plan;
    private List<PostDependency> postDependencies = new ArrayList<>();
    private List<UserException> errors = new ArrayList<>();

    public PlanCreator(TaskBuilder taskBuilder, BatchUpdate.Targets targets) {
        this.plan = new PlanImpl(taskBuilder, targets);
    }

    @Override
    public void addMeta(BatchUpdate.MetaBasis metaBasis) {
        //System.out.println("PlanCreator.addMeta "+metaBasis.getName().orElse(MetaNameImpl.create("-")));
        final Actions.EntityProtocolAction createMeta = plan.create(LocalActionKind.CREATE_META);
        metaBasis.getMethods()
                .map(this::addMetaMethodAction)
                .forEach(createMeta::addDependency);
        metaBasis.getName()
                .map(updateAddressName(createMeta))
                .ifPresent(createMeta::setName);;
    }

    private Actions.SimpleTargetProtocolAction addMetaMethodAction(BatchUpdate.MetaBasis.MethodPart methodPart) {
        //System.out.println("PlanCreator.addMetaMethodAction");
        final Actions.SimpleTargetProtocolAction metaMethod = plan.create(LocalActionKind.ADD_META_METHOD);
        methodPart.getCode()
                .map(code -> plan
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetValue<String>>>create(LocalActionKind.SET_METHOD_CODE)
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetValue<String>>>updateDataPortal(new ActionsSetValue<>(code)))
                .ifPresent(metaMethod::addDependency);
        methodPart.getName()
                .map(updateAddressName(metaMethod));
                //.ifPresent(metaMethod::setName);   // TODO set name of method
        return metaMethod;
    }

    @Override
    public void addClass(BatchUpdate.ClassBasis classBasis) {
        //System.out.println("PlanCreator.addClass "+classBasis.getName().orElse(InstanceClassNameImpl.create("-")));
        Actions.EntityProtocolAction createClass = plan.create(LocalActionKind.CREATE_CLASS);

        classBasis.getName()
                .map(updateAddressName(createClass))
                .ifPresent(createClass::setName);
        classBasis.getFields()
                .map(this::setClassFieldAction)
                .forEach(createClass::addDependency);
        classBasis.getRelations()
                .map(this::setClassRelationAction)
                .forEach(createClass::addDependency);
    }

    @Override
    public void addInstance(BatchUpdate.InstanceBasis instanceBasis) {
        System.out.println("PlanCreator.addInstance pre "+instanceBasis.getName().orElse(null));
        final Actions.SimpleTargetProtocolAction createInstance = plan.create(LocalActionKind.CREATE_INSTANCE);

        instanceBasis.getClassName().ifPresent(instanceClassName -> {
            plan.getActionByName(instanceClassName).ifPresent(instanceClass -> {
                System.out.println("PlanCreator.addInstance post "+instanceClassName);
                resolveDependencyAndTarget(createInstance, instanceClassName, instanceClass);
            });
            instanceBasis.getAlias()
                    .map(updateAddressName(createInstance))
                    .ifPresent(aliasName -> {
                        System.out.println("PlanCreator.addInstance alias="+aliasName);
                        final Actions.DataPortalTargetProtocolAction<Actions.AddInstanceAliasData> addAlias = plan.create(LocalActionKind.ADD_ALIAS);
                        addAlias.updateDataPortal(new ActionsAddInstanceAliasData(aliasName,  instanceBasis.getName().orElse(null)));
                        plan.getActionByName(instanceClassName).ifPresent(instanceClass -> {
                            System.out.println("PlanCreator.addInstance resolved "+instanceClassName);
                            resolveDependencyAndTarget(addAlias, instanceClassName, instanceClass);
                        });
                        createInstance.addDependencyAndResolver(addAlias, values -> {
                            final Message.Value instanceAddress = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                            return Optional.of(NameValue.create(instanceBasis.getName().orElse(null), instanceAddress));
                        });
                    });

        });

        instanceBasis.getValues()
                .map(valuePart -> {
                    final Actions.DataPortalTargetProtocolAction<Actions.AddValueData> addValue = plan.create(LocalActionKind.ADD_VALUE);
                    addValue.updateDataPortal(new ActionsAddValueData(valuePart.getValue().orElse(null), valuePart.getName().orElse(null)));
                    valuePart.getName().ifPresent(fieldName -> {
                        plan.getActionByName(fieldName).ifPresent(fieldAction -> {
                            fieldAction.addDependencyAndResolver(addValue, values -> {
                                final Message.Value targetClassAddress = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                                return Optional.of(NameValue.create(fieldName, targetClassAddress));
                            });
                        });
                    });
                    return addValue;
                })
                .forEach(createInstance::addDependency);
        instanceBasis.getRelations()
                .flatMap(relationPart ->
                        relationPart.getInstances().map(instanceName -> {

                        final Actions.DataPortalTargetProtocolAction<Actions.AddRelationData> addRelation = plan.create(LocalActionKind.ADD_RELATION);

                        relationPart.getName().ifPresent(relationName -> {
                            addRelation.updateDataPortal(new ActionsAddRelationData(
                                    relationName,
                                    instanceName));
                            plan.getActionByName(relationName).ifPresent(baseAction -> baseAction.addDependency(addRelation));
                        });
                        return addRelation;

                    }
                ))
                .forEach(createInstance::addDependency);
    }

    @Override
    public void parseError(UserException parseError) {
        parseError.printStackTrace();
        errors.add(parseError);
    }

    private Actions.SimpleTargetProtocolAction setClassFieldAction(BatchUpdate.ClassBasis.FieldPart fieldPart) {
        final Actions.SimpleTargetProtocolAction addClassFieldAction = plan.create(LocalActionKind.ADD_CLASS_FIELD);
        fieldPart.getName()
                .map(updateAddressName(addClassFieldAction))
                .map(fieldName -> plan
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<Field.FieldName>>>create(LocalActionKind.SET_FIELD_NAME)
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<Field.FieldName>>>updateDataPortal(new ActionsSetNameData<Field.FieldName>(fieldName, null)))
                .ifPresent(addClassFieldAction::addDependency);
        return addClassFieldAction;
    }

    private Actions.DataPortalTargetProtocolAction<Actions.AddClassRelationData> setClassRelationAction(BatchUpdate.ClassBasis.RelationPart relationPart) {
        final Actions.DataPortalTargetProtocolAction<Actions.AddClassRelationData> addClassRelationAction = plan.create(LocalActionKind.ADD_CLASS_RELATION);
        final ActionsAddClassRelationData dataPortal = new ActionsAddClassRelationData();
        addClassRelationAction.updateDataPortal(dataPortal);
        relationPart.getRelationType().ifPresent(dataPortal::setRelationType);
        relationPart.getName()
                .map(updateAddressName(addClassRelationAction))
                .map(relationName -> plan
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<RelationClass.RelationName>>>create(LocalActionKind.SET_RELATION_NAME)
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<RelationClass.RelationName>>>updateDataPortal(new ActionsSetNameData<RelationClass.RelationName>(relationName, null)))
                .ifPresent(addClassRelationAction::addDependency);

        relationPart.getTargetInstanceClassIdentity()
                .ifPresent(dataPortal::setTargetInstanceClassIdentityName);

/*
        relationPart.getTargetInstanceClassIdentity()
                .ifPresent(instanceClassName -> postDependencies.add(actionByName -> actionByName
                        .actionByName(instanceClassName)
                        .ifPresent(baseAction -> baseAction
                                .addDependency(addClassRelationAction))));
*/

        relationPart.getTargetInstanceClassIdentity()
                .ifPresent(instanceClassName -> postDependencies.add(actionByName -> actionByName
                        .actionByName(instanceClassName)
                        .ifPresent(dependencyAction -> {
                            dependencyAction.addDependencyAndResolver(addClassRelationAction, values -> {
                                final Message.Value targetClassAddress = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                                return Optional.of(NameValue.create(instanceClassName, targetClassAddress));
                            });
                        })));


        //TODO add resolver for dependency name
        return addClassRelationAction;
    }

    private static class ActionsAddClassRelationData implements Actions.AddClassRelationData {
        private RelationClass.RelationType relationType = RelationClass.RelationType.ASSOCIATION;
        private InstanceClass.InstanceClassIdentity relationClass;
        private InstanceClassNameImpl instanceClassName;

        @Override public RelationClass.RelationType getRelationType() {return relationType;}
        @Override public InstanceClass.InstanceClassIdentity getRelatedClass() {return relationClass;}
        public void setRelationType(RelationClass.RelationType relationType) {this.relationType = relationType;}
        public void setRelationClass(InstanceClass.InstanceClassIdentity relationClass) {this.relationClass = relationClass;}

        @Override
        public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            actionContext.getValue(instanceClassName)
                    .map(Message.Value::asAddress)
                    .map(address -> (InstanceClass.InstanceClassIdentity) address)
                    .ifPresent(this::setRelationClass);
        }

        public void setTargetInstanceClassIdentityName(InstanceClassNameImpl instanceClassName) {
            this.instanceClassName = instanceClassName;
        }

        @Override public boolean canStart() { return relationClass != null;}
    }

    private static class ActionsSetNameData<N extends Name> implements Actions.SetNameData<N> {
        private N name;
        private Name namedInContext;

        public ActionsSetNameData(Name namedInContext, N name) {
            this.namedInContext = namedInContext;
            this.name = name;
        }

        @Override public N getName() {return name;}

        @Override public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            if (namedInContext!=null)
                actionContext.getValue(namedInContext).ifPresent(value -> name = value.asName());
        }

        public boolean filterName(Name name) { return namedInContext.equals(name); }
    }

    private static class ActionsAddInstanceAliasData implements Actions.AddInstanceAliasData {
        private Instance.InstanceIdentity instanceIdentity;
        private Name instanceName;
        private Name name;

        public ActionsAddInstanceAliasData(Name name, Name instanceName) {
            this.name = name;
            this.instanceName = instanceName;
        }

        @Override public Instance.InstanceIdentity getInstanceIdentity() {return instanceIdentity;}
        @Override public Name getName() {return name;}

        @Override public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            actionContext.getValue(instanceName).ifPresent(value -> instanceIdentity = value.asAddress());
        }

        @Override public boolean canStart() { return instanceIdentity != null;}
    }

    private static class ActionsAddValueData implements Actions.AddValueData {
        private Field.FieldIdentity fieldIdentity;
        private Message.Value value;
        private Name fieldNameInContext;

        public ActionsAddValueData(Message.Value value, Name fieldNameInContext) {
            this.value = value;
            this.fieldNameInContext = fieldNameInContext;
        }

        @Override public Field.FieldIdentity getFieldIdentity() {return fieldIdentity;}
        @Override public Message.Value getValue() {return value;}

        @Override public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            if (fieldNameInContext!=null)
                actionContext.getValue(fieldNameInContext).ifPresent(fieldIdentityValue -> this.fieldIdentity = fieldIdentityValue.asAddress());
        }

        public boolean canStart() { return fieldIdentity != null; };
    }

    private static class ActionsAddRelationData implements Actions.AddRelationData {
        private RelationClass.RelationClassIdentity relationClassIdentity;
        private Instance.InstanceIdentity instanceIdentity;

        private Name relationClassIdentityNameInContext;
        private Name instanceIdentityNameInContext;

        public ActionsAddRelationData(Name relationClassIdentityNameInContext, Name instanceIdentityNameInContext) {
            this.relationClassIdentityNameInContext = relationClassIdentityNameInContext;
            this.instanceIdentityNameInContext = instanceIdentityNameInContext;
        }

        @Override public RelationClass.RelationClassIdentity getRelationClassIdentity() {return relationClassIdentity;}
        @Override public Instance.InstanceIdentity getInstanceIdentity() {return instanceIdentity;}
        public void updateRelationClassIdentityFromContextValue(Message.Value relationClassIdentity) {
            this.relationClassIdentity = relationClassIdentity.asAddress();
        }
        public void updateInstanceIdentityFromContextValue(Message.Value instanceIdentity) {
            this.instanceIdentity = instanceIdentity.asAddress();
        }

        @Override
        public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            Optional.ofNullable(relationClassIdentityNameInContext)
                    .ifPresent(setValueFetchedFromContext(actionContext, this::updateRelationClassIdentityFromContextValue));
            Optional.ofNullable(instanceIdentityNameInContext)
                    .ifPresent(setValueFetchedFromContext(actionContext, this::updateInstanceIdentityFromContextValue));
        }

        public boolean canStart() { return relationClassIdentity != null && instanceIdentity != null; };
    }

    private static class ActionsSetValue<O extends Object> implements Actions.SetValue<O> {
        private O value;
        private Name namedInContext;

        public ActionsSetValue(O value, Name namedInContext) {
            this.value = value;
            this.namedInContext = namedInContext;
        }

        public ActionsSetValue(O value) {
            this(value, null);
        }

        @Override public O getValue() {return value; }

        @Override public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            if (namedInContext!=null)
                actionContext.getValue(namedInContext).ifPresent(value -> this.value = value.getValue());
        }
    }

    PlanImpl getPlan() throws UserException {
        if (!errors.isEmpty()) {
            if (errors.size()==0)
                throw errors.get(0);
            throw new UserException(ErrorClass.REST_SERVICE, ErrorKind.PARSE_ERROR, MessageValue.values(errors.stream().map(MessageValue::error)));
        }
        postDependencies.stream()
                .forEach(postDependency -> postDependency.processDependency((ActionByName) plan::getActionByName));
        return plan;
    }

    interface PostDependency {
        void processDependency(ActionByName actionByName);
    }

    interface ActionByName {
        <A extends Actions.BaseAction, N extends Name> Optional<A> actionByName(N name);
    }

    private <N extends Name> Function<N, N> updateAddressName(Actions.BaseAction createClass) {
        return name -> {
            createClass.addAddressNameUpdate((actionContext, address) -> actionContext.updateAddressName(name, address));
            return name;
        };
    }

    private void resolveDependencyAndTarget(Actions.BaseAction dependentAction, Name resolveName, Actions.BaseAction actionWhosAddressNeedsToBeResolved) {
        if (resolveName == null)
            return;
        actionWhosAddressNeedsToBeResolved.addDependencyAndResolver(dependentAction, resolveTarget(resolveName));
        dependentAction.setFindTargetInContext(findTargetInContext(resolveName));
    }

    Function<Iterable<Message.Value>, Optional<NameValue>> resolveTarget(Name targetName) {
        return values -> {
            final Message.Value targetClassAddress = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
            return Optional.of(NameValue.create(targetName, targetClassAddress));
        };
    }

    Function<BatchUpdate.ActionContext, Optional<Address>> findTargetInContext(Name targetName)  {
        return actionContext -> actionContext.getValue(targetName).map(Message.Value::asAddress);
    }

}
