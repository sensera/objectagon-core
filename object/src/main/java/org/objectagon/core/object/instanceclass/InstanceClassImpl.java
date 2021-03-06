package org.objectagon.core.object.instanceclass;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.*;
import org.objectagon.core.object.field.FieldService;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.object.instanceclass.data.InstanceClassDataImpl;
import org.objectagon.core.object.relationclass.RelationClassService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.event.EventServiceImpl;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.KeyValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.objectagon.core.object.instanceclass.MethodClassImpl.findMethodClassByMethodIdentity;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassImpl extends EntityImpl<InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData, StandardVersion, InstanceClassImpl.InstanceClassWorker> implements InstanceClass {

    enum InstanceClassTaskName implements Task.TaskName {
        LOOKUP_METHOD_FIELD_VALUES_PARAMS
    }

    static final MethodMessageValueTransform methodMessageValueTransform = new MethodMessageValueTransform();

    Service.ServiceName eventService;

    @Override protected boolean logLevelCheck(WorkerContextLogKind logKind) {return false;}

    public InstanceClassImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, InstanceClass.DATA_TYPE);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        getReceiverCtrl().lookupAddressByAlias(EventServiceImpl.EVENT_SERVICE_NAME).ifPresent(address -> eventService = (Service.ServiceName) address);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected InstanceClassData createNewData(Optional<StandardVersion> version) {
        return InstanceClassDataImpl.create(getAddress(), version.orElse(StandardVersion.create(0L)));
    }

    @Override
    protected InstanceClassWorker createWorker(WorkerContext workerContext) {
        return new InstanceClassWorker(workerContext, eventService);
    }

    public class InstanceClassWorker extends EntityWorkerImpl {
        private Service.ServiceName eventService;

        public InstanceClassWorker(WorkerContext workerContext, Service.ServiceName eventService) {
            super(workerContext);
            if (eventService==null) throw new NullPointerException("eventService is null!");
            this.eventService = eventService;
        }

        public void broadCastNameChange(InstanceClassIdentity identity, InstanceClassName instanceClassName) {
            createEventServiceProtocolSend(eventService).broadcast(
                    StorageServices.SEARCH_NAME_EVENT,
                    SearchServiceProtocol.MessageName.NAME_CHANGED_EVENT,
                    MessageValue.address(Identity.IDENTITY, identity),
                    MessageValue.name(instanceClassName));
        }

        public MethodProtocol.Send createMethodProtocolSend(Method.MethodIdentity methodIdentity) {
            if (methodIdentity==null) throw new NullPointerException("methodIdentity is null!");
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,MethodProtocol>createReceiver(
                            MethodProtocol.METHOD_PROTOCOL)
                    .createSend(() -> getWorkerContext().createTargetComposer(methodIdentity));
        }

        public InstanceProtocol.Send createInstanceProtocol(Instance.InstanceIdentity instanceIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,InstanceProtocol>createReceiver(InstanceProtocol.INSTANCE_PROTOCOL)
                    .createSend(() -> getWorkerContext().createTargetComposer(instanceIdentity));
        }


    }

    @Override
    protected InstanceClassData upgrade(InstanceClassData data, DataRevision<InstanceClassIdentity, StandardVersion> newDataRevision, Transaction transaction) {
        return data; // TODO fix this
    }

    @Override
    protected InstanceClassIdentity createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(InstanceClassIdentityImpl::new);
    }

    @Override
    protected void handle(InstanceClassWorker worker) {
        triggerBuilder(worker)
                .trigger(InstanceClassProtocol.MessageName.GET_FIELDS, read(this::getFields))
                .trigger(InstanceClassProtocol.MessageName.GET_RELATIONS, read(this::getRelations))
                .trigger(InstanceClassProtocol.MessageName.ADD_FIELD, write(this::<ChangeInstanceClass>addField, this::createField))
                .trigger(InstanceClassProtocol.MessageName.ADD_RELATION, write(this::<ChangeInstanceClass>setRelationClass, this::createRelationClass))
                .trigger(InstanceClassProtocol.MessageName.SET_RELATION, write(this::<ChangeInstanceClass>setRelationClass))
                .trigger(InstanceClassProtocol.MessageName.GET_NAME, read(this::getName))
                .trigger(InstanceClassProtocol.MessageName.SET_NAME, write(this::<ChangeInstanceClass>setName))
                .trigger(InstanceClassProtocol.MessageName.CREATE_INSTANCE, this::createInstance)
                .trigger(InstanceClassProtocol.MessageName.GET_INSTANCE_BY_ALIAS, read(this::getInstanceByAlias))
                .trigger(InstanceClassProtocol.MessageName.ADD_INSTANCE_ALIAS, write(this::<ChangeInstanceClass>addInstanceAlias))
                .trigger(InstanceClassProtocol.MessageName.REMOVE_INSTANCE_ALIAS, write(this::<ChangeInstanceClass>removeInstanceAlias))
                .trigger(InstanceClassProtocol.MessageName.ADD_METHOD, write(this::<ChangeInstanceClass>addMethod))
                .trigger(InstanceClassProtocol.MessageName.REMOVE_METHOD, write(this::<ChangeInstanceClass>removeMethod))
                .trigger(InstanceClassProtocol.MessageName.INVOKE_METHOD, read(this::invokeMethod))
                .orElse(w -> super.handle(w));
    }

    private void getInstanceByAlias(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) throws UserException {
        Name instanceAlias = instanceClassWorker.getValue(InstanceClassProtocol.ALIAS_NAME).asName();
        final Optional<Instance.InstanceIdentity> instanceByAliasName = instanceClassData.getInstanceByAliasName(instanceAlias);
        instanceClassWorker.trace("InstanceClassImpl.getInstanceByAlias", MessageValue.name(instanceAlias), MessageValue.address(Transaction.TRANSACTION, instanceClassWorker.currentTransaction()), MessageValue.address(instanceByAliasName.orElse(null)));
        instanceClassWorker.replyWithParam(MessageValue.address(Instance.INSTANCE_IDENTITY, instanceByAliasName
                        .orElseThrow(() -> new UserException(ErrorClass.INSTANCE_CLASS, ErrorKind.INSTANCE_NOT_FOUND, instanceClassWorker.getValue(InstanceClassProtocol.ALIAS_NAME)))));
    }

    private void addInstanceAlias(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        Name instanceAlias = instanceClassWorker.getValue(InstanceClassProtocol.ALIAS_NAME).asName();
        instanceClassWorker.trace("InstanceClassImpl.addInstanceAlias", MessageValue.name(instanceAlias), MessageValue.address(instanceClassWorker.currentTransaction()));
        Instance.InstanceIdentity instanceIdentity = instanceClassWorker.getValue(Instance.INSTANCE_IDENTITY).asAddress();
        changeInstanceClass.addInstanceAlias(instanceIdentity, instanceAlias);
    }

    private void removeInstanceAlias(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        Name instanceAlias = instanceClassWorker.getValue(InstanceClassProtocol.ALIAS_NAME).asName();
        changeInstanceClass.removeInstanceAlias(instanceAlias);
    }

    private void getName(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        //System.out.println("InstanceClassImpl.getName "+instanceClassData.getName()+" ¤"+instanceClassWorker.currentTransaction());
        instanceClassWorker.replyWithParam(MessageValue.name(InstanceClass.INSTANCE_CLASS_NAME, instanceClassData.getName()));
    }

    private void setName(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        InstanceClassName instanceClassName = instanceClassWorker.getValue(InstanceClass.INSTANCE_CLASS_NAME).asName();
        changeInstanceClass.setName(instanceClassName);
        instanceClassWorker.broadCastNameChange(getAddress(), instanceClassName);
        //System.out.println("InstanceClassImpl.setName "+instanceClassName+" ¤"+instanceClassWorker.currentTransaction());
    }

    private void getFields(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        Message.Value values = MessageValue.values(Field.FIELDS,
                instanceClassData.getFields().stream().map(MessageValue::address).collect(Collectors.toList())
        );
        instanceClassWorker.replyWithParam(values);
    }

    private void getRelations(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        List<Message.Value> collect = instanceClassData.getRelations().stream().map(MessageValue::address).collect(Collectors.toList());
        instanceClassWorker.replyWithParam(MessageValue.values(Relation.RELATIONS,
                collect
        ));
    }

    private Optional<Task> createField(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        return Optional.of(instanceClassWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, FieldService.NAME)
                .create(MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, getAddress())));
    }

    private void addField(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        Field.FieldIdentity fieldIdentity = MessageValueFieldUtil.create(preparedValues).getValueByField(StandardField.ADDRESS).asAddress();
        changeInstanceClass.addField(fieldIdentity);
    }

    private Optional<Task> createRelationClass(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        return Optional.of(instanceClassWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, RelationClassService.NAME)
                .create(instanceClassWorker.getValue(RelationClass.RELATION_TYPE),
                        instanceClassWorker.getValue(RelationClass.INSTANCE_CLASS_TO),
                        MessageValue.address(RelationClass.INSTANCE_CLASS_FROM, getAddress())));
    }

    private void setRelationClass(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        RelationClass.RelationClassIdentity relationClassIdentity = MessageValueFieldUtil.create(preparedValues).getValueByField(StandardField.ADDRESS).asAddress();
        changeInstanceClass.addRelation(relationClassIdentity);
    }

    private void addMethod(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        Method.MethodIdentity methodIdentity = instanceClassWorker.getValue(Method.METHOD_IDENTITY).asAddress();
        List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings = methodMessageValueTransform.createFieldMappingsTransformer()
                .transform(instanceClassWorker.getValue(InstanceClassProtocol.METHOD_FIELD_MAPPINGS));
        List<KeyValue<Method.ParamName, Message.Value>> defaultValues = methodMessageValueTransform.createValuesTransformer()
                .transform(instanceClassWorker.getValue(InstanceClassProtocol.METHOD_DEFAULT_MAPPINGS));
        changeInstanceClass.addMethod(methodIdentity, fieldMappings, defaultValues);
    }

    private void removeMethod(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        Method.MethodIdentity methodIdentity = MessageValueFieldUtil.create(preparedValues).getValueByField(Method.METHOD_IDENTITY).asAddress();
        changeInstanceClass.removeMethod(methodIdentity);
    }

    private void createInstance(InstanceClassWorker entityWorker) {
        entityWorker.start(
                entityWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, InstanceService.NAME)
                        .create(MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, getAddress()))
        );
    }

    private void invokeMethod(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) throws UserException {
        //System.out.println("InstanceClassImpl.invokeMethod ******************");
        final Method.MethodIdentity methodIdentity = instanceClassWorker.getValue(Method.METHOD_IDENTITY).asAddress();
        final Instance.InstanceIdentity instanceIdentity = instanceClassWorker.getValue(Instance.INSTANCE_IDENTITY).asAddress();
        final Optional<MethodClass> methodClassByMethodIdentityOptional = findMethodClassByMethodIdentity(instanceClassData.getMethods(), methodIdentity);
        if (!methodClassByMethodIdentityOptional.isPresent())
            throw new UserException(ErrorClass.INSTANCE_CLASS, ErrorKind.FAILED_TO_INVOKE_METHOD);

        final MethodClass methodClass = methodClassByMethodIdentityOptional.get();
        final List<KeyValue<Method.ParamName, Message.Value>> paramNameValueList = methodMessageValueTransform.createValuesTransformer().transform(instanceClassWorker.getValue(InstanceClassProtocol.METHOD_DEFAULT_MAPPINGS));
        paramNameValueList.addAll(methodClass.getDefaultValues());

        if (methodClassByMethodIdentityOptional.get().getFieldMappings().isEmpty()) {
             instanceClassWorker.createMethodProtocolSend(methodIdentity)
                     .invoke(paramNameValueList)
                     .addFailedAction(instanceClassWorker::failed)
                     .addSuccessAction(methodReplySuccessAction(instanceClassWorker, methodClass, instanceIdentity))
                     .start();
            return;
        }

        final TaskBuilder.SequenceBuilder sequence = instanceClassWorker.getTaskBuilder()
                .sequence(InstanceClassTaskName.LOOKUP_METHOD_FIELD_VALUES_PARAMS);

        methodClass.getFieldMappings().stream().forEach(paramNameFieldIdentityKeyValue -> {
            sequence.addTask(instanceClassWorker.createInstanceProtocol(instanceIdentity).getValue(paramNameFieldIdentityKeyValue.getValue())
                    .addSuccessAction((messageName, values) -> {
                        final Message.Value valueByField = MessageValueFieldUtil.create(values).getValueByField(FieldValue.VALUE);
                        final Message.Value foundValue = valueByField.asValues().values().iterator().next();
                        //System.out.println("InstanceClassImpl.invokeMethod looked up "+paramNameFieldIdentityKeyValue.getKey()+"="+foundValue.asText());
                        final KeyValue<Method.ParamName, Message.Value> keyValue = methodMessageValueTransform.createKeyValue(paramNameFieldIdentityKeyValue.getKey(), foundValue);
                        final List<KeyValue<Method.ParamName, Message.Value>> remove = paramNameValueList.stream()
                                .filter(paramNameValueKeyValue -> paramNameValueKeyValue.getKey().equals(keyValue.getKey()))
                                .collect(Collectors.toList());
                        remove.forEach(paramNameValueList::remove);
                        paramNameValueList.add(keyValue);
                    }));
        });
        final Task task = sequence.create();
        task.addFailedAction(instanceClassWorker::failed);
        task.addSuccessAction((messageName, values) -> {
            System.out.println("InstanceClassImpl.invokeMethod SUCCESS ******************* params ******************'");
            paramNameValueList.stream().forEach(paramNameValueKeyValue -> System.out.println("InstanceClassImpl.invokeMethod "+paramNameValueKeyValue.getKey()+"="+paramNameValueKeyValue.getValue().asText()));
            instanceClassWorker.createMethodProtocolSend(methodIdentity)
                    .invoke(paramNameValueList)
                        .addFailedAction(instanceClassWorker::failed)
                        .addSuccessAction(methodReplySuccessAction(instanceClassWorker, methodClass, instanceIdentity))
                        .start();
        });
        task.start();

    }

    private Task.SuccessAction methodReplySuccessAction(InstanceClassWorker instanceClassWorker, MethodClass methodClass, Instance.InstanceIdentity instanceIdentity) {
        return (messageName1, values) -> {
            final Message.Value replyParams = MessageValueFieldUtil.create(values).getValueByField(Method.PARAMS);
            if (replyParams.isUnknown()) {
                instanceClassWorker.replyOk();
                return;
            }
            final Message.Value methodDefaultMappingsValue = MessageValueFieldUtil.create(replyParams.asValues()).getValueByField(InstanceClassProtocol.METHOD_DEFAULT_MAPPINGS);
            final List<KeyValue<Method.ParamName, Message.Value>> paramValues = methodMessageValueTransform.createValuesTransformer().transform(methodDefaultMappingsValue);
            if (paramValues.isEmpty()) {
                instanceClassWorker.replyOk();
                return;
            }
            final TaskBuilder.SequenceBuilder sequence = instanceClassWorker.getTaskBuilder()
                    .sequence(InstanceClassTaskName.LOOKUP_METHOD_FIELD_VALUES_PARAMS);
            paramValues.stream().forEach(paramNameValueKeyValue -> {
                final Optional<Field.FieldIdentity> field = methodClass.getFieldMappings().stream()
                        .filter(paramNameFieldIdentityKeyValue -> paramNameFieldIdentityKeyValue.getKey().equals(paramNameValueKeyValue.getKey()))
                        .map(KeyValue::getValue)
                        .findAny();
                if (!field.isPresent()) {
                    instanceClassWorker.failed(ErrorClass.INSTANCE_CLASS, ErrorKind.INCONSISTENCY, Arrays.asList(paramNameValueKeyValue.getValue(), MessageValue.name(paramNameValueKeyValue.getKey())));
                    return;
                }
                sequence.addTask(instanceClassWorker.createInstanceProtocol(instanceIdentity).setValue(field.get(), paramNameValueKeyValue.getValue()));
            });

            instanceClassWorker.start(
                    sequence.create()
            );
        };
    }

}
