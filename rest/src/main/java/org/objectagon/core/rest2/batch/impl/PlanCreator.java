package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.object.*;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;
import org.objectagon.core.utils.NameValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.objectagon.core.rest2.batch.impl.actions.AbstractActions.RESOLVE_INSTANCE_ID;
import static org.objectagon.core.rest2.batch.impl.actions.AbstractActions.RESOLVE_META_CLASS_ID;
import static org.objectagon.core.rest2.batch.impl.actions.AbstractActions.RESOLVE_METHOD_ID;

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
        final Actions.EntityProtocolAction createMeta = plan.create(LocalActionKind.CREATE_META);
        metaBasis.getMethods()
                .map(this::addMetaMethodAction)
                .forEach(createMeta::addDependency);
        metaBasis.getName()
                .map(updateAddressName(createMeta))
                .ifPresent(createMeta::setName);;
    }

    private Actions.SimpleTargetProtocolAction addMetaMethodAction(BatchUpdate.MetaBasis.MethodPart methodPart) {
        final Actions.SimpleTargetProtocolAction metaMethod = plan.create(LocalActionKind.ADD_META_METHOD);
        methodPart.getCode()
                .map(code -> plan
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetValue<String>>>create(LocalActionKind.SET_METHOD_CODE)
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetValue<String>>>updateDataPortal(new ActionsSetValue<>(code)))
                .ifPresent(metaMethod::addDependency);
        methodPart.getName()
                .map(updateAddressName(metaMethod))
                .ifPresent(metaMethod::setName);
        methodPart.getInvokeParams()
                .map(this::addMethodParam)
                .forEach(metaMethod::addDependency);
        return metaMethod;
    }

    private Actions.DataPortalTargetProtocolAction<Actions.AddMethodParamData> addMethodParam(Method.InvokeParam invokeParam) {
        final Actions.DataPortalTargetProtocolAction<Actions.AddMethodParamData> addMethodParam = plan.create(LocalActionKind.ADD_METHOD_PARAM);
        addMethodParam.updateDataPortal(new ActionsAddMethodParamData(
                invokeParam.getName(),
                invokeParam.getField(),
                invokeParam.getDefaultValue().orElse(null)));
        return addMethodParam;
    }

    @Override
    public void addClass(BatchUpdate.ClassBasis classBasis) {
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
        classBasis.getMethods()
                .map(this::setClassMethodAction)
                .forEach(createClass::addDependency);
    }

    @Override
    public void addInstance(BatchUpdate.InstanceBasis instanceBasis) {
        final Actions.SimpleTargetProtocolAction createInstance = plan.create(LocalActionKind.CREATE_INSTANCE);

        instanceBasis.getName().ifPresent(createInstance::setName);

        instanceBasis.getClassName().ifPresent(instanceClassName -> {
                plan.getActionByName(instanceClassName).ifPresent(instanceClass -> {
                    resolveDependencyAndTarget(createInstance, instanceClassName, instanceClass);
            });
            instanceBasis.getAlias()
                    .map(updateAddressName(createInstance))
                    .ifPresent(aliasName -> {
                        final Actions.DataPortalTargetProtocolAction<Actions.AddInstanceAliasData> addAlias = plan.create(LocalActionKind.ADD_ALIAS);
                        addAlias.updateDataPortal(new ActionsAddInstanceAliasData(aliasName,  instanceBasis.getName().orElse(null)));
                        plan.getActionByName(instanceClassName).ifPresent(instanceClass -> {
                            resolveDependencyAndTarget(addAlias, instanceClassName, instanceClass);
                        });
                        createInstance.addDependencyAndResolver(addAlias, values -> {
                            final Message.Value instanceAddress = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS);
                            return Optional.of(NameValue.create(instanceBasis.getName().orElse(null), instanceAddress));
                        });
                    });

        });

        instanceBasis.getValues()
                .filter(valuePart -> instanceBasis.getClassName().isPresent())
                .map(this::addInstanceValue)
                .forEach(createInstance::addDependency);

        instanceBasis.getRelations()
                .map(this::addInstanceRelation)
                //.peek(action -> action.setFindTargetInContext(findTargetInContext(instanceBasis.getName().get())))
                .filter(Objects::nonNull)
                .forEach(createInstance::addDependency);
    }

    private Actions.DataPortalTargetProtocolAction<Actions.AddRelationData> addInstanceRelation(BatchUpdate.InstanceBasis.RelationPart relationPart) {
            try {
                final RelationClass.RelationName relationName = relationPart.getName()
                        .orElseThrow(userException("relation class name"));
                final Name instanceName = relationPart.getInstanceName()
                        .orElseThrow(userException("instance name"));

                final Actions.DataPortalTargetProtocolAction<Actions.AddRelationData> addRelation =
                        plan.create(LocalActionKind.ADD_RELATION);
                addRelation.updateDataPortal(new ActionsAddRelationData(relationName, instanceName));

                resolveDependencyFunc(addRelation)
                        .apply(relationName)
                        .map(addToPostDependencies(addRelation));
                resolveDependencyFunc(addRelation)
                        .apply(instanceName)
                        .map(addToPostDependencies(addRelation));
                return addRelation;
            } catch (UserException e) {
                 e.printStackTrace();
                return null;
            }
    }

    private Supplier<UserException> userException(String name) {
        return () -> new UserException(ErrorClass.BATCH_UPDATE, ErrorKind.NAME_MISSING, MessageValue.text(name));
    }

    private Actions.DataPortalTargetProtocolAction<Actions.AddValueData> addInstanceValue(
            BatchUpdate.InstanceBasis.ValuePart valuePart) {
        final Actions.DataPortalTargetProtocolAction<Actions.AddValueData> addValue = plan.create(LocalActionKind.ADD_VALUE);

        final ActionsAddValueData actionsAddValueData = new ActionsAddValueData();
        addValue.updateDataPortal(actionsAddValueData);

        valuePart.getName()
                .map(actionsAddValueData::setFieldName)
                .flatMap(resolveDependencyFunc(addValue))
                .map(addToPostDependencies(addValue));

        valuePart.getValue()
                .ifPresent(actionsAddValueData::setValue);


        return addValue;
    }

    @Override
    public void parseError(UserException parseError) {
        parseError.printStackTrace();
        errors.add(parseError);
    }

    private Actions.SimpleTargetProtocolAction setClassFieldAction(BatchUpdate.ClassBasis.FieldPart fieldPart) {
        final Actions.SimpleTargetProtocolAction addClassFieldAction = plan.create(LocalActionKind.ADD_CLASS_FIELD);
        fieldPart.getName()
                .map(fieldName -> { addClassFieldAction.setName(fieldName); return fieldName; })
                .map(updateAddressName(addClassFieldAction))
                .map(fieldName -> plan
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<Field.FieldName>>>create(LocalActionKind.SET_FIELD_NAME)
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<Field.FieldName>>>updateDataPortal(new ActionsSetNameData<>(null, fieldName)))
                .ifPresent(addClassFieldAction::addDependency);
        fieldPart.getType()
                .map(fieldType -> {
                    final Actions.DataPortalTargetProtocolAction<Actions.SetValue> setFieldType = plan.create(LocalActionKind.SET_FIELD_TYPE);
                    setFieldType.updateDataPortal(new ActionsSetValue<>(fieldType));
                    return setFieldType;
                })
                .ifPresent(addClassFieldAction::addDependency);
        fieldPart.getDefaultValue()
                .map(defaultValue -> {
                    final Actions.DataPortalTargetProtocolAction<Actions.SetValue> setFieldType = plan.create(LocalActionKind.SET_FIELD_DEFAULT_VALUE);
                    setFieldType.updateDataPortal(new ActionsSetValue<>(defaultValue));
                    return setFieldType;
                })
                .ifPresent(addClassFieldAction::addDependency);
        return addClassFieldAction;
    }

    private Actions.DataPortalTargetProtocolAction<Actions.AddClassRelationData> setClassRelationAction(BatchUpdate.ClassBasis.RelationPart relationPart) {
        final Actions.DataPortalTargetProtocolAction<Actions.AddClassRelationData> addClassRelationAction = plan.create(LocalActionKind.ADD_CLASS_RELATION);
        final ActionsAddClassRelationData dataPortal = new ActionsAddClassRelationData();
        addClassRelationAction.updateDataPortal(dataPortal);
        relationPart.getRelationType().ifPresent(dataPortal::setRelationType);
        relationPart.getName()
                .map(dataPortal::updateRelationName)
                .map(updateAddressName(addClassRelationAction))
                .map(relationName -> plan
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<RelationClass.RelationName>>>create(LocalActionKind.SET_RELATION_NAME)
                        .<Actions.DataPortalTargetProtocolAction<Actions.SetNameData<RelationClass.RelationName>>>updateDataPortal(new ActionsSetNameData<RelationClass.RelationName>(null, relationName)))
                .ifPresent(addClassRelationAction::addDependency);

        relationPart.getTargetInstanceClassName()
                .ifPresent(dataPortal::setTargetInstanceClassIdentityName);

        relationPart.getTargetInstanceClassName()
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

    private Actions.DataPortalTargetProtocolAction<Actions.AddClassMethodData> setClassMethodAction(BatchUpdate.ClassBasis.MethodPart methodPart) {
        final Actions.DataPortalTargetProtocolAction<Actions.AddClassMethodData> addClassMethodAction = plan.create(LocalActionKind.ADD_CLASS_METHOD);
        final ActionsAddClassMethodData dataPortal = new ActionsAddClassMethodData();
        addClassMethodAction.updateDataPortal(dataPortal);
        methodPart.getName()
                .map(updateAddressName(addClassMethodAction)) // TODO check if this is necessary
                .map(dataPortal::setMethodName)
                .flatMap(resolveDependencyAndTargetFunc(addClassMethodAction))
                .map(addResolverToPostDependencies(addClassMethodAction));

        methodPart.getMetaMethodName()
                .map(dataPortal::setMetaMethodName)
                .flatMap(resolveDependencyFunc(addClassMethodAction))
                .map(addResolverToPostDependencies(addClassMethodAction));

        methodPart.getMetaName()
                .map(dataPortal::setMetaName)
                .flatMap(resolveDependencyFunc(addClassMethodAction))
                .map(addResolverToPostDependencies(addClassMethodAction));

        methodPart.getMappedParams()
                .map(methodPartDetail -> KeyValueUtil.createKeyValue(methodPartDetail.getParamName(), methodPartDetail.getFieldName()))
                .forEach(dataPortal::addFieldMapping);

        methodPart.getMappedParams()
                .filter(methodPartDetail -> methodPartDetail.getDefaultValue().isPresent())
                .map(methodPartDetail -> KeyValueUtil.createKeyValue(methodPartDetail.getParamName(), methodPartDetail.getDefaultValue().get()))
                .forEach(dataPortal::addDefaultValue);

        methodPart.getMappedParams()
                .forEach(methodPartDetail -> {
                    postDependencies.add(actionByName -> actionByName
                            .actionByName(methodPartDetail.getParamName())
                            .ifPresent(dependencyAction -> {
                                dependencyAction.addDependencyAndResolver(addClassMethodAction, transferResolvedAddressToAction(methodPartDetail.getParamName()));
                            }));
                });

        return addClassMethodAction;
    }

    private <N extends Name> Function<N, N> addResolverToPostDependencies(BatchUpdate.Action action) {
        return name -> {
            postDependencies.add(actionByName -> actionByName.actionByName(name)
                    .orElseThrow(() -> new SevereError(ErrorClass.REST_SERVICE, ErrorKind.NOT_FOUND, MessageValue.name(name)))
                    .addDependencyAndResolver(action, transferResolvedAddressToAction(name)));
            System.out.println("PlanCreator.addResolverToPostDependencies '"+name+" "+action);
            return name;
        };
    }

    private <N extends Name> Function<N, N> addToPostDependencies(BatchUpdate.Action action) {
        return name -> {
            postDependencies.add(actionByName -> actionByName.actionByName(name)
                    .orElseThrow(() -> new SevereError(ErrorClass.REST_SERVICE, ErrorKind.NOT_FOUND, MessageValue.name(name)))
                    .addDependencyAndResolver(action, transferResolvedAddressToAction(name)));
            System.out.println("PlanCreator.addResolverToPostDependencies '"+name+" "+action);
            return name;
        };
    }

    private Consumer<Name> resolveDependencyAndTarget(Actions.BaseAction baseAction) {
        return name -> plan.resolveActionByName(name)
                .ifPresent(actionAndName -> resolveDependencyAndTarget(baseAction, actionAndName.getName(), actionAndName.getAction()));
    }

    private <N extends Name> Function<N,Optional<N>> resolveDependencyAndTargetFunc(Actions.BaseAction baseAction) {
        return name -> plan.resolveActionByName(name)
                .map(actionAndName -> {
                    resolveDependencyAndTarget(baseAction, actionAndName.getName(), actionAndName.getAction());
                    return name;
                });
    }

    private <N extends Name> Function<N,Optional<N>> resolveDependencyFunc(Actions.BaseAction baseAction) {
        return name -> plan.resolveActionByName(name)
                .map(actionAndName -> {
                    //actionAndName.getAction().addDependency(baseAction);
                    actionAndName.getAction().addDependencyAndResolver(baseAction, transferResolvedAddressToAction(name));
                    return Optional.<N>empty();
                })
                .orElse(Optional.of(name));
    }

    private Function<Iterable<Message.Value>, Optional<NameValue>> transferResolvedAddressToAction(Name name) {
        return values ->
                MessageValueFieldUtil.create(values).getValueByFieldOption(StandardField.ADDRESS)
                .map(targetClassAddress -> NameValue.create(name, targetClassAddress));
    }

    private static class ActionsAddClassMethodData implements Actions.AddClassMethodData {
        Method.MethodName methodName;
        Method.MethodName metaMethodName;
        Meta.MetaName metaName;
        Method.MethodIdentity methodIdentity;
        Meta.MetaIdentity metaIdentity;
        private List<KeyValue<Method.ParamName, Field.FieldName>> fieldNameMappings = new ArrayList<>();
        private List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldIdentityMappings = new ArrayList<>();
        private List<KeyValue<Method.ParamName, Message.Value>> defaultValues = new ArrayList<>();

        public ActionsAddClassMethodData() {}

        @Override public Method.MethodIdentity getMethodIdentity() {return methodIdentity;}
        void setMethodIdentity(Method.MethodIdentity methodIdentity) { this.methodIdentity = methodIdentity;}
        public void setMetaIdentity(Meta.MetaIdentity metaIdentity) {this.metaIdentity = metaIdentity;}
        Meta.MetaName setMetaName(Meta.MetaName metaName) {this.metaName = metaName; return metaName;}
        Method.MethodName setMethodName(Method.MethodName methodName) { this.methodName = methodName; return methodName; }
        Method.MethodName setMetaMethodName(Method.MethodName metaMethodName) {this.metaMethodName = metaMethodName; return metaMethodName;}
        private boolean sameMeta(Method.MethodIdentity methodIdentity) {
            if (metaIdentity==null) //TODO Need to handle multiple async name collisions
                System.out.println("ActionsAddClassMethodData.sameMeta !!!!!!!!!!!!!!!! "+methodIdentity+" Verification om null metaIdentity!!!!!!!!!!!!!!!!!!!");
            return Optional.ofNullable(metaIdentity)
                    .filter(metaIdentity1 -> metaIdentity1.equals(methodIdentity.getMetaIdentity()))
                    .isPresent();
        }

        @Override public Stream<KeyValue<Method.ParamName, Field.FieldIdentity>> getFieldMappings() {return fieldIdentityMappings.stream();}
        @Override public Stream<KeyValue<Method.ParamName, Message.Value>> getDefaultValues() {return defaultValues.stream();}

        void addFieldMapping(KeyValue<Method.ParamName, Field.FieldName> fieldMapping) {
            fieldNameMappings.add(fieldMapping);
        }

        void addFieldIdentityMapping(KeyValue<Method.ParamName, Field.FieldIdentity> fieldMapping) {
            fieldIdentityMappings.add(fieldMapping);
        }

        void addDefaultValue(KeyValue<Method.ParamName, Message.Value> defaultValue) {
            defaultValues.add(defaultValue);
        }

        @Override public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            actionContext.getValue(methodName)
                    .map(Message.Value::asAddress)
                    .filter(address -> address instanceof Method.MethodIdentity)
                    .map(address -> (Method.MethodIdentity) address)
                    .filter(this::sameMeta)
                    .ifPresent(this::setMethodIdentity);
            actionContext.getValue(RESOLVE_METHOD_ID)
                    .map(Message.Value::asAddress)
                    .filter(address -> address instanceof Method.MethodIdentity)
                    .map(address -> (Method.MethodIdentity) address)
                    .filter(this::sameMeta)
                    .ifPresent(this::setMethodIdentity);
            actionContext.getValue(metaName)
                    .map(Message.Value::asAddress)
                    .filter(address -> address instanceof Meta.MetaIdentity)
                    .map(address -> (Meta.MetaIdentity) address)
                    .ifPresent(this::setMetaIdentity);
            actionContext.getValue(RESOLVE_META_CLASS_ID)
                    .map(Message.Value::asAddress)
                    .filter(address -> address instanceof Meta.MetaIdentity)
                    .map(address -> (Meta.MetaIdentity) address)
                    .ifPresent(this::setMetaIdentity);
            fieldNameMappings.stream()
                    .map(paramNameFieldNameKeyValue -> actionContext.getValue(paramNameFieldNameKeyValue.getKey())
                            .map(value -> KeyValueUtil.createKeyValue(paramNameFieldNameKeyValue.getKey(), (Field.FieldIdentity)value.asAddress())))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(this::addFieldIdentityMapping);
        }
        @Override public boolean canStart() { return methodIdentity != null;}

        @Override public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("\n");
            sb.append("  Class: "+getClass().getSimpleName()).append("\n");
            sb.append("  methodName '"+methodName+"'").append("\n");
            sb.append("  metaName '"+metaName+"'").append("\n");
            sb.append("  methodIdentity '"+methodIdentity+"'").append("\n");
            fieldNameMappings.forEach(printKeyValue(sb));
/*
            private List<KeyValue<Method.ParamName, Field.FieldName>> fieldNameMappings = new ArrayList<>();
            private List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldIdentityMappings = new ArrayList<>();
            private List<KeyValue<Method.ParamName, Message.Value>> defaultValues = new ArrayList<>();
*/

            return sb.toString();
        }
    }

    private static Consumer<KeyValue<Method.ParamName, Field.FieldName>> printKeyValue(StringBuffer sb) {
        return paramNameFieldNameKeyValue ->
                                          sb.append("  "+paramNameFieldNameKeyValue.getKey())
                                                  .append(" ")
                                                  .append(paramNameFieldNameKeyValue.getValue())
                                                  .append("\n");
    }

    private static class ActionsAddClassRelationData implements Actions.AddClassRelationData {
        private RelationClass.RelationName relationName;
        private RelationClass.RelationType relationType = RelationClass.RelationType.ASSOCIATION;
        private InstanceClass.InstanceClassIdentity relationClass;
        private InstanceClass.InstanceClassName instanceClassName;

        @Override public RelationClass.RelationType getRelationType() {return relationType;}
        @Override public InstanceClass.InstanceClassIdentity getRelatedClass() {return relationClass;}
        public void setRelationType(RelationClass.RelationType relationType) {this.relationType = relationType;}
        public void setRelationClass(InstanceClass.InstanceClassIdentity relationClass) {this.relationClass = relationClass;}
        RelationClass.RelationName updateRelationName(RelationClass.RelationName relationName) {
            this.relationName = relationName;
            return relationName;
        }

        @Override
        public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            actionContext.getValue(instanceClassName)
                    .map(Message.Value::asAddress)
                    .map(address -> (InstanceClass.InstanceClassIdentity) address)
                    .ifPresent(this::setRelationClass);
        }

        public void setTargetInstanceClassIdentityName(InstanceClass.InstanceClassName instanceClassName) {
            this.instanceClassName = instanceClassName;
        }

        @Override public boolean canStart() { return relationClass != null;}

        @Override public boolean filterName(Name name) {
            return Objects.equals(relationName, name);
        }
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

        public boolean filterName(Name name) { return namedInContext != null && namedInContext.equals(name); }
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
        private Instance.InstanceIdentity instanceIdentity;
        private Message.Value value;
        private Field.FieldName fieldName;

        public ActionsAddValueData() {}

        public void setFieldIdentity(Field.FieldIdentity fieldIdentity) {this.fieldIdentity = fieldIdentity;}
        @Override public Field.FieldIdentity getFieldIdentity() {return fieldIdentity;}
        @Override public Message.Value getValue() {return value;}
        public void setInstanceIdentity(Instance.InstanceIdentity instanceIdentity) {
            this.instanceIdentity = instanceIdentity;
        }

        @Override public void updateFromContext(BatchUpdate.ActionContext actionContext) {
            Optional.ofNullable(RESOLVE_INSTANCE_ID)
                    .map(actionContext::getValue)
                    .flatMap(actionContextValue -> actionContextValue)
                    .map(Message.Value::asAddress)
                    .map(address -> (Instance.InstanceIdentity) address)
                    .ifPresent(this::setInstanceIdentity);

            Optional.ofNullable(fieldName)
                    .map(actionContext::getValue)
                    .flatMap(actionContextValue -> actionContextValue)
                    .map(Message.Value::asAddress)
                    .map(address -> (Field.FieldIdentity) address)
                    //.filter(this::sameInstanceClassIdentity)
                    .ifPresent(this::setFieldIdentity);
        }

        private boolean sameInstanceClassIdentity(Field.FieldIdentity fieldIdentity) {
            return fieldIdentity.getInstanceClassIdentity().equals(instanceIdentity.getInstanceClassIdentity());
        }

        public boolean canStart() { return fieldIdentity != null && instanceIdentity != null; };

        public Field.FieldName setFieldName(Field.FieldName fieldName) {
            this.fieldName = fieldName;
            return fieldName;
        }

        public  Message.Value setValue(Message.Value value) {
            this.value = value;
            return value;
        }

        @Override public String toString() {
            return "ActionsAddValueData{" +
                    "fieldIdentity=" + fieldIdentity +
                    ", instanceIdentity=" + instanceIdentity +
                    ", value=" + value +
                    ", fieldName=" + fieldName +
                    '}';
        }
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
            actionContext.getValue(StandardName.name("main1")).ifPresent(value -> {
                System.out.println("ActionsAddRelationData.updateFromContext here main1="+value.asAddress());
            });

            Optional.ofNullable(relationClassIdentityNameInContext)
                    .ifPresent(setValueFetchedFromContext(actionContext, this::updateRelationClassIdentityFromContextValue));
            Optional.ofNullable(instanceIdentityNameInContext)
                    .ifPresent(setValueFetchedFromContext(actionContext, this::updateInstanceIdentityFromContextValue));
        }

        public boolean canStart() { return relationClassIdentity != null && instanceIdentity != null; };

        @Override public String toString() {
            return "ActionsAddRelationData{" +
                    "relationClassIdentity=" + relationClassIdentity +
                    ", instanceIdentity=" + instanceIdentity +
                    ", relationClassIdentityNameInContext=" + relationClassIdentityNameInContext +
                    ", instanceIdentityNameInContext=" + instanceIdentityNameInContext +
                    '}';
        }
    }

    private static class ActionsAddMethodParamData implements Actions.AddMethodParamData {
        private Method.ParamName paramName;
        private Message.Field field;
        private Message.Value defaultValue;

        public ActionsAddMethodParamData(Method.ParamName paramName, Message.Field field, Message.Value defaultValue) {
            this.paramName = paramName;
            this.field = field;
            this.defaultValue = defaultValue;
        }

        @Override public Method.ParamName getParamName() {return paramName;}
        @Override public Message.Field getField() {return field;}
        @Override public Message.Value getDefaultValue() {return defaultValue;}

        @Override
        public void updateFromContext(BatchUpdate.ActionContext actionContext) {}
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

    private <N extends Name> Function<N, N> updateAddressName(Actions.BaseAction createdNamedTarget) {
        return name -> {
            createdNamedTarget.addAddressNameUpdate((actionContext, address) -> actionContext.updateAddressName(name, address));
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

    <T> Function<T,T> printFunc(String msg) {
        return t -> {
            System.out.println(msg + ": " + t);
            return t;
        };
    }
}
