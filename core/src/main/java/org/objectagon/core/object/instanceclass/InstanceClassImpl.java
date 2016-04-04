package org.objectagon.core.object.instanceclass;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.*;
import org.objectagon.core.object.field.FieldService;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.object.instanceclass.data.InstanceClassDataImpl;
import org.objectagon.core.object.relationclass.RelationClassService;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
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

    public InstanceClassImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected InstanceClassData createNewData() {
        return InstanceClassDataImpl.create(getAddress(), StandardVersion.create(0L));
    }

    @Override
    protected InstanceClassWorker createWorker(WorkerContext workerContext) {
        return new InstanceClassWorker(workerContext);
    }

    public class InstanceClassWorker extends EntityWorkerImpl {
        public InstanceClassWorker(WorkerContext workerContext) {
            super(workerContext);
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
                .trigger(InstanceClassProtocol.MessageName.CREATE_INSTANCE, this::createInstance)
                .orElse(w -> super.handle(w));
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
        Message.Value fieldName = instanceClassWorker.getValue(Field.FIELD_NAME);
        System.out.println("InstanceClassImpl.createField ********************************************** "+fieldName);
        return Optional.of(instanceClassWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, FieldService.NAME).create(fieldName));
    }

    private void addField(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        System.out.println("InstanceClassImpl.addField ****************************************************'");
        Field.FieldIdentity fieldIdentity = MessageValueFieldUtil.create(preparedValues).getValueByField(Identity.IDENTITY).asAddress();
        changeInstanceClass.addField(fieldIdentity);
    }

    private Optional<Task> createRelationClass(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData) {
        Message.Value relationName = instanceClassWorker.getValue(RelationClass.RELATION_NAME);
        return Optional.of(instanceClassWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, RelationClassService.NAME).create(relationName));
    }

    private void addRelationClass(InstanceClassWorker instanceClassWorker, InstanceClassData instanceClassData, ChangeInstanceClass changeInstanceClass, Message.Values preparedValues) {
        RelationClass.RelationClassIdentity relationClassIdentity = MessageValueFieldUtil.create(preparedValues).getValueByField(Identity.IDENTITY).asAddress();
        changeInstanceClass.addRelation(relationClassIdentity);
    }

    private void createInstance(InstanceClassWorker entityWorker) {
        System.out.println("InstanceClassImpl.createInstance ****************************************");
        entityWorker.start(
                entityWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, InstanceService.NAME)
                        .create(MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, getAddress()))
        );
    }
}
