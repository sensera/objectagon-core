package org.objectagon.core.object.relationclass;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.*;
import org.objectagon.core.object.relation.RelationService;
import org.objectagon.core.object.relationclass.data.RelationClassDataImpl;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Arrays;
import java.util.Optional;

import static org.objectagon.core.storage.entity.EntityService.EXTRA_ADDRESS_CONFIG_NAME;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassImpl extends EntityImpl<RelationClass.RelationClassIdentity, RelationClass.RelationClassData, StandardVersion, RelationClassImpl.RelationClassEntityWorker> implements RelationClass {

    private enum RelationClassTasks implements Task.TaskName { CreateRelation, DestroyRelation,  }

    public RelationClassImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, RelationClass.DATA_TYPE);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected RelationClassData createNewData(Optional<StandardVersion> version) {
        return RelationClassDataImpl.create(getAddress(), version.orElse(StandardVersion.create(0L)));
    }

    @Override
    protected RelationClassData upgrade(RelationClassData data, DataVersion<RelationClassIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data;
    }

    @Override
    protected RelationClassEntityWorker createWorker(WorkerContext workerContext) {
        return new RelationClassEntityWorker(workerContext);
    }

    public class RelationClassEntityWorker extends EntityWorkerImpl {
        public RelationClassEntityWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public InstanceProtocol.Internal createInstanceProtocolInternal(Instance.InstanceIdentity instanceIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress,InstanceProtocol>createReceiver(InstanceProtocol.INSTANCE_PROTOCOL)
                    .createInternal(() -> getWorkerContext().createTargetComposer(instanceIdentity));
        }
    }

    @Override
    protected RelationClassIdentity createAddress(Configurations... configurations) {
        FindNamedConfiguration finder = FindNamedConfiguration.finder(configurations);
        RelationClassProtocol.ConfigRelationClass configInstance = finder.getConfigurationByName(EXTRA_ADDRESS_CONFIG_NAME);
        return finder.createConfiguredAddress((serverId, timestamp, addressId) ->
                new RelationClassIdentityImpl(configInstance.getInstanceClassIdentity(RelationDirection.RELATION_FROM),
                        configInstance.getInstanceClassIdentity(RelationDirection.RELATION_TO),
                        configInstance.getRelationType(), serverId, timestamp, addressId)
        );
    }

    @Override
    protected void handle(RelationClassEntityWorker worker) {
        triggerBuilder(worker)
                .trigger(RelationClassProtocol.MessageName.GET_NAME, read(this::getName))
                .trigger(RelationClassProtocol.MessageName.SET_NAME, write(this::<RelationClassDataChange>setName))
                .trigger(RelationClassProtocol.MessageName.CREATE_RELATION, read(this::createRelation))
                .trigger(RelationClassProtocol.MessageName.DESTROY_RELATION, read(this::destroyRelation))
                .orElse(w -> super.handle(w));
    }

    private void getName(RelationClassEntityWorker fieldEntityWorker, RelationClassData relationClassData) {
        fieldEntityWorker.replyWithParam(MessageValue.name(RELATION_NAME, relationClassData.getName()));
    }

    private void setName(RelationClassEntityWorker fieldEntityWorker, RelationClassData relationClassData, RelationClassDataChange relationClassDataChange, Message.Values values) {
        relationClassDataChange.setName(MessageValueFieldUtil.create(values).getValueByField(RELATION_NAME).asName());
    }

    private void createRelation(RelationClassEntityWorker entityWorker, RelationClassData relationClassData) throws UserException {
        Instance.InstanceIdentity relationFrom = entityWorker.getValue(RelationClass.INSTANCE_CLASS_FROM).asAddress();
        Instance.InstanceIdentity relationTo = entityWorker.getValue(RelationClass.INSTANCE_CLASS_TO).asAddress();
        if (!relationClassData.getInstanceClassIdentity(RelationDirection.RELATION_FROM).equals(relationFrom.getInstanceClassIdentity()))
            throw new UserException(ErrorClass.RELATION_CLASS, ErrorKind.INCONSISTENCY, entityWorker.getValue(RelationClass.INSTANCE_CLASS_FROM));
        if (!relationClassData.getInstanceClassIdentity(RelationDirection.RELATION_TO).equals(relationTo.getInstanceClassIdentity()))
            throw new UserException(ErrorClass.RELATION_CLASS, ErrorKind.INCONSISTENCY, entityWorker.getValue(RelationClass.INSTANCE_CLASS_TO));
        TaskBuilder.SequenceBuilder sequence = entityWorker.getTaskBuilder().sequence(RelationClassTasks.CreateRelation);
        sequence.addTask(entityWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, RelationService.NAME).create(
                MessageValue.address(RelationClass.INSTANCE_CLASS_FROM, relationFrom),
                MessageValue.address(RelationClass.INSTANCE_CLASS_TO, relationTo),
                MessageValue.address(RelationClass.RELATION_CLASS_IDENTITY, getAddress())
        ));
        sequence.addTask((messageName, values) -> {
            Relation.RelationIdentity relationIdentity = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
            return Optional.of(entityWorker.createInstanceProtocolInternal(relationTo).setRelation(relationIdentity));
        });
        entityWorker.start(
            sequence.create()
        );
    }

    private void destroyRelation(RelationClassEntityWorker entityWorker, RelationClassData relationClassData) throws UserException {
        Instance.InstanceIdentity relationFrom = entityWorker.getValue(RelationClass.INSTANCE_CLASS_FROM).asAddress();
        Relation.RelationIdentity relationIdentity = entityWorker.getValue(Relation.RELATION_IDENTITY).asAddress();

        if (!relationClassData.getInstanceClassIdentity(RelationDirection.RELATION_FROM).equals(relationFrom.getInstanceClassIdentity()))
            throw new UserException(ErrorClass.RELATION_CLASS, ErrorKind.INCONSISTENCY, entityWorker.getValue(RelationClass.INSTANCE_CLASS_FROM));

        TaskBuilder.SequenceBuilder sequence = entityWorker.getTaskBuilder().sequence(RelationClassTasks.DestroyRelation);
        Arrays.asList(RelationDirection.values()).forEach(relationDirection ->
                sequence.addTask(entityWorker.createInstanceProtocolInternal(relationIdentity.getInstanceIdentity(relationDirection)).dropRelation(relationIdentity))
        );
        entityWorker.start(
            sequence.create()
        );
    }


}
