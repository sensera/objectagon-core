package org.objectagon.core.object.instanceclass;

import org.objectagon.core.msg.Message;
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
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassImpl extends EntityImpl<InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData, StandardVersion, InstanceClassImpl.InstanceClassWorker> implements InstanceClass {

    Service.ServiceName eventService;

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
            System.out.println("InstanceClassWorker.broadCastNameChange "+identity+" "+instanceClassName);
            createEventServiceProtocolSend(eventService).broadcast(
                    StorageServices.SEARCH_NAME_EVENT,
                    SearchServiceProtocol.MessageName.NAME_CHANGED_EVENT,
                    MessageValue.address(Identity.IDENTITY, identity),
                    MessageValue.name(instanceClassName));
        }
    }

    @Override
    protected InstanceClassData upgrade(InstanceClassData data, DataVersion<InstanceClassIdentity, StandardVersion> newDataVersion, Transaction transaction) {
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
                .trigger(InstanceClassProtocol.MessageName.ADD_RELATION, write(this::<ChangeInstanceClass>addRelationClass, this::createRelationClass))
                .trigger(InstanceClassProtocol.MessageName.GET_NAME, read(this::getName))
                .trigger(InstanceClassProtocol.MessageName.SET_NAME, write(this::<ChangeInstanceClass>setName))
                .trigger(InstanceClassProtocol.MessageName.CREATE_INSTANCE, this::createInstance)
                .orElse(w -> super.handle(w));
    }

    private void getName(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        instanceClassWorker.replyWithParam(MessageValue.name(InstanceClass.INSTANCE_CLASS_NAME, instanceClassData.getName()));
    }

    private void setName(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        InstanceClassName instanceClassName = instanceClassWorker.getValue(InstanceClass.INSTANCE_CLASS_NAME).asName();
        changeInstanceClass.setName(instanceClassName);
        instanceClassWorker.broadCastNameChange(getAddress(), instanceClassName);
    }

    private void getFields(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        Message.Value values = MessageValue.values(Field.FIELDS,
                instanceClassData.getFields().map(MessageValue::address).collect(Collectors.toList())
        );
        instanceClassWorker.replyWithParam(values);
    }

    private void getRelations(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        List<Message.Value> collect = instanceClassData.getRelations().map(MessageValue::address).collect(Collectors.toList());
        instanceClassWorker.replyWithParam(MessageValue.values(Relation.RELATIONS,
                collect
        ));
    }

    private Optional<Task> createField(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        return Optional.of(instanceClassWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, FieldService.NAME)
                .create(MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, getAddress())));
    }

    private void addField(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        Field.FieldIdentity fieldIdentity = MessageValueFieldUtil.create(preparedValues).getValueByField(Identity.IDENTITY).asAddress();
        changeInstanceClass.addField(fieldIdentity);
    }

    private Optional<Task> createRelationClass(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        return Optional.of(instanceClassWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, RelationClassService.NAME)
                .create(
                        instanceClassWorker.getValue(RelationClass.RELATION_TYPE),
                        instanceClassWorker.getValue(RelationClass.RELATION_CLASS_IDENTITY),
                        MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, getAddress())));
    }

    private void addRelationClass(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        RelationClass.RelationClassIdentity relationClassIdentity = MessageValueFieldUtil.create(preparedValues).getValueByField(Identity.IDENTITY).asAddress();
        changeInstanceClass.addRelation(relationClassIdentity);
    }

    private void createInstance(InstanceClassWorker entityWorker) {
        entityWorker.start(
                entityWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, InstanceService.NAME)
                        .create(MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, getAddress()))
        );
    }
}
