package org.objectagon.core.object.instance;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.*;
import org.objectagon.core.object.instance.data.InstanceDataImpl;
import org.objectagon.core.object.instanceclass.MethodMessageValueTransform;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.EntityProtocol;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.objectagon.core.object.fieldvalue.FieldValueUtil.findField;
import static org.objectagon.core.object.fieldvalue.RelationUtil.findRelation;
import static org.objectagon.core.object.fieldvalue.RelationUtil.getDirection;
import static org.objectagon.core.storage.entity.EntityService.EXTRA_ADDRESS_CONFIG_NAME;


/**
 * Created by christian on 2015-10-20.
 */
public class InstanceImpl extends EntityImpl<Instance.InstanceIdentity,Instance.InstanceData,StandardVersion,InstanceImpl.InstanceWorker> implements Instance {

    static final MethodMessageValueTransform methodMessageValueTransform = new MethodMessageValueTransform();

    private enum InstanceTasks implements Task.TaskName { DestroyRelations }

    public InstanceImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, Instance.DATA_TYPE);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected InstanceData createNewData(Optional<StandardVersion> version) {
        return InstanceDataImpl.create(getAddress(), version.orElse(StandardVersion.create(0L)));
    }

    @Override
    protected InstanceData upgrade(InstanceData data, DataRevision<InstanceIdentity, StandardVersion> newDataRevision, Transaction transaction) {
        return data; //TODO improve
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
    }

    @Override
    protected InstanceIdentity createAddress(Configurations... configurations) {
        FindNamedConfiguration finder = FindNamedConfiguration.finder(configurations);
        InstanceProtocol.ConfigInstance configInstance = finder.getConfigurationByName(EXTRA_ADDRESS_CONFIG_NAME);
        return finder.createConfiguredAddress((serverId, timestamp, addressId) ->
                new InstanceIdentityImpl(configInstance.getInstanceClassIdentity(), serverId, timestamp, addressId)
        );
    }

    @Override
    protected void handle(InstanceWorker worker) {
        triggerBuilder(worker)
                .trigger(InstanceProtocol.MessageName.GET_VALUE, read(this::getValue))
                .trigger(InstanceProtocol.MessageName.SET_VALUE, write(this::setValue, this::createValue))
                .trigger(InstanceProtocol.MessageName.ADD_FIELD, write(this::addField))
                .trigger(InstanceProtocol.MessageName.REMOVE_FIELD, write(this::removeField, this::dropFieldValues))
                .trigger(InstanceProtocol.MessageName.ADD_RELATION, write(this::addRelation, this::createRelation))
                .trigger(InstanceProtocol.MessageName.SET_RELATION, write(this::addRelation))
                .trigger(InstanceProtocol.MessageName.REMOVE_RELATION, read(this::removeRelation))
                .trigger(InstanceProtocol.MessageName.DROP_RELATION, write(this::dropRelation))
                .trigger(InstanceProtocol.MessageName.GET_RELATION, read(this::getRelation))
                .trigger(InstanceProtocol.MessageName.INVOKE_METHOD, read(this::invokeMethod))
                .orElse(w -> super.handle(w));
    }

    private Optional<Task> dropFieldValues(InstanceWorker instanceWorker, InstanceData instanceData) {
        return Optional.of(instanceWorker.getTaskBuilder()
                .message(
                    InstanceProtocol.MessageName.DROP_FIELD_VALUES,
                    FieldValueProtocol.FIELD_VALUE_PROTOCOL,
                    instanceWorker.getValue(Field.FIELD_IDENTITY).<Field.FieldIdentity>asAddress(),
                    EntityProtocol.Send::delete)
                .create()
        );
    }

    private void addField(InstanceWorker instanceWorker, InstanceData instanceData, InstanceDataChange change, Message.Values values) {
        change.addField(MessageValueFieldUtil.create(values).getValueByField(Field.FIELD_IDENTITY).asAddress());
    }

    private void removeField(InstanceWorker instanceWorker, InstanceData instanceData, InstanceDataChange change, Message.Values values) {
        change.removeField(MessageValueFieldUtil.create(values).getValueByField(Field.FIELD_IDENTITY).asAddress());
    }

    @Override
    protected void updateDataAndVersion(InstanceData newData, DataRevision<InstanceIdentity, StandardVersion> newDataRevision) {
        //System.out.println("InstanceImpl.updateDataAndVersion newData.version="+newData.getVersion());
        super.updateDataAndVersion(newData, newDataRevision);
    }

    @Override public <VV extends Version> InstanceData createNewDataWithVersion(VV versionForNewData) {
        final InstanceData newDataWithVersion = super.createNewDataWithVersion(versionForNewData);
        //System.out.println("InstanceImpl.createNewDataWithVersion version="+newDataWithVersion.getVersion());
        return newDataWithVersion;
    }

    private void getValue(InstanceWorker instanceWorker, InstanceData instanceData) throws UserException {
        //System.out.println("InstanceImpl("+getAddress()+").getValue trans="+instanceWorker.currentTransaction()+"");
        final Field.FieldIdentity fieldIdentity = instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress();
        //System.out.println("InstanceImpl.getValue field="+fieldIdentity);
        try {
            FieldValue.FieldValueIdentity fieldValueIdentity = getFieldValueIdentity(fieldIdentity,instanceData);
            instanceWorker.createFieldValueProtocolForward(fieldValueIdentity).getValue();
        } catch (UserException e) {
            if (ErrorKind.FIELD_NOT_FOUND.equals(e.getErrorKind())) {  //TODO exception is not program flow
                //System.out.println("InstanceImpl.getValue NOT_FOUND will get default value");
                instanceWorker.createFieldProtocolForward(fieldIdentity).getDefaultValue();
            } else
                throw e;
        }
    }

    private Optional<Task> createValue(InstanceWorker instanceWorker, InstanceData instanceData) {
        final Message.Value value = instanceWorker.getValue(FieldValue.VALUE).asValues().values().iterator().next();
        final Field.FieldIdentity fieldIdentity = instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress();
        instanceWorker.trace("InstanceImpl.setValue", value, MessageValue.address(fieldIdentity), MessageValue.address(instanceWorker.currentTransaction()));
        //System.out.println("InstanceImpl("+getAddress()+").createValue "+value+" field="+fieldIdentity+" trans="+ instanceWorker.currentTransaction());
        try {
            FieldValue.FieldValueIdentity fieldValueIdentity = getFieldValueIdentity(fieldIdentity, instanceData);
            instanceWorker.createFieldValueProtocolForward(fieldValueIdentity)
                    .setValue(value);
            //instanceWorker.replyOk();
        } catch (UserException e) {
            if (ErrorKind.FIELD_NOT_FOUND.equals(e.getErrorKind())) {
                return Optional.of(instanceWorker.createFieldProtocolSend(fieldIdentity)
                        .createFieldValue(getAddress(), value));
            } else
                instanceWorker.replyWithError(e);
        }
        return Optional.empty();
    }

    private void addRelation(InstanceWorker instanceWorker, InstanceData instanceData, InstanceDataChange change, Message.Values values) {
        Relation.RelationIdentity relationIdentity = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
        //System.out.println("InstanceImpl.addRelation "+relationIdentity+" <"+instanceWorker.currentTransaction()+">");
        change.addRelation(relationIdentity);
    }

    private Optional<Task> createRelation(InstanceWorker instanceWorker, InstanceData instanceData) {
        InstanceIdentity targetIdentity = instanceWorker.getValue(StandardField.ADDRESS).asAddress();
        RelationClass.RelationClassIdentity relationClassIdentity = instanceWorker.getValue(RelationClass.RELATION_CLASS_IDENTITY).asAddress();
        //System.out.println("InstanceImpl.createRelation target="+targetIdentity);
        //System.out.println("InstanceImpl.createRelation type="+relationClassIdentity);
        return Optional.of(instanceWorker.createRelationClassProtocolSend(relationClassIdentity).createRelation(getAddress(), targetIdentity));
    }

    private void dropRelation(InstanceWorker instanceWorker, InstanceData instanceData, InstanceDataChange change, Message.Values values) {
        Relation.RelationIdentity relationIdentity = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
        //System.out.println("InstanceImpl.dropRelation "+relationIdentity+" <"+instanceWorker.currentTransaction()+">");
        change.removeRelation(relationIdentity);
    }

    private void removeRelation(InstanceWorker instanceWorker, InstanceData instanceData) throws UserException {
        InstanceIdentity targetIdentity = instanceWorker.getValue(StandardField.ADDRESS).asAddress();
        RelationClass.RelationClassIdentity relationClassIdentity = instanceWorker.getValue(RelationClass.RELATION_CLASS_IDENTITY).asAddress();
        //System.out.println("InstanceImpl.removeRelation target="+targetIdentity);
        //System.out.println("InstanceImpl.removeRelation type="+relationClassIdentity);
        List<Relation.RelationIdentity> relationsToRemove = instanceData.getRelations()
                .filter(r -> r.getRelationClassIdentity().equals(relationClassIdentity))
                .filter(r -> r.getInstanceIdentity(RelationClass.RelationDirection.RELATION_TO).equals(targetIdentity))
                .collect(Collectors.toList());
        if (relationsToRemove.isEmpty()) {
            //System.out.println("InstanceImpl.removeRelation found NO relations to remove!");
            instanceWorker.replyOk();
            return;
        }
        //System.out.println("InstanceImpl.removeRelation found "+relationsToRemove.size()+" relation(s) to remove!");
        TaskBuilder.SequenceBuilder sequence = instanceWorker.getTaskBuilder().sequence(InstanceTasks.DestroyRelations);
        relationsToRemove.stream().forEach(relationIdentity -> {
            sequence.addTask(instanceWorker.createRelationClassProtocolSend(relationClassIdentity).destroyRelation(getAddress(), relationIdentity));
        });
        instanceWorker.start(sequence.start());
    }

    private void getRelation(InstanceWorker instanceWorker, InstanceData instanceData) throws UserException {
        //System.out.println("InstanceImpl.getRelation <"+instanceWorker.currentTransaction()+">");
        try {
            List<InstanceIdentity> instanceIdentities = getRelations(
                    instanceWorker.getValue(RelationClass.RELATION_CLASS_IDENTITY).asAddress(),
                    instanceData);

            final List<Message.Value> instanceList = instanceIdentities.stream()
                    .map(instanceIdentity -> MessageValue.address(Instance.INSTANCE_IDENTITY, instanceIdentity))
                    .collect(Collectors.toList());

            instanceWorker.replyWithParam(MessageValue.values(instanceList));
        } catch (UserException e) {
            if (ErrorKind.FIELD_NOT_FOUND.equals(e.getErrorKind())) {
                //System.out.println("InstanceImpl.getValue NOT_FOUND will get default value");
                instanceWorker.createFieldProtocolForward(instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress()).getDefaultValue();
            } else
                throw e;
        }
    }

    private void invokeMethod(InstanceWorker instanceWorker, InstanceData instanceData) throws UserException {
        //System.out.println("InstanceImpl.invokeMethod  <"+instanceWorker.currentTransaction()+">");
        instanceWorker.start(
                instanceWorker.createInstanceClassProtocolForward(getAddress().getInstanceClassIdentity())
                        .invokeMethod(
                                instanceWorker.getValue(Method.METHOD_IDENTITY).asAddress(),
                                getAddress(),
                                methodMessageValueTransform.createValuesTransformer().transform(instanceWorker.getValue(InstanceClassProtocol.METHOD_DEFAULT_MAPPINGS)))
        );
    }

    private void setValue(InstanceWorker instanceWorker, InstanceData instanceData, InstanceDataChange change, Message.Values values) {
        FieldValue.FieldValueIdentity fieldValueIdentity = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
        instanceWorker.trace("InstanceImpl.setValue", MessageValue.address(fieldValueIdentity), MessageValue.address(instanceWorker.currentTransaction()));
        change.addField(fieldValueIdentity);
    }

    @Override
    protected InstanceWorker createWorker(WorkerContext workerContext) {
        return new InstanceWorker(workerContext);
    }


    public class InstanceWorker extends EntityWorkerImpl {
        public InstanceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public FieldValueProtocol.Forward createFieldValueProtocolForward(FieldValue.FieldValueIdentity fieldValueIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress, FieldValueProtocol>createReceiver(FieldValueProtocol.FIELD_VALUE_PROTOCOL)
                    .createForward(() -> getWorkerContext().createForwardComposer(fieldValueIdentity));
        }

        public FieldProtocol.Forward createFieldProtocolForward(Field.FieldIdentity fieldIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress, FieldProtocol>createReceiver(FieldProtocol.FIELD_PROTOCOL)
                    .createForward(() -> getWorkerContext().createForwardComposer(fieldIdentity));
        }

        public FieldProtocol.Send createFieldProtocolSend(Field.FieldIdentity fieldIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress, FieldProtocol>createReceiver(FieldProtocol.FIELD_PROTOCOL)
                    .createSend(() -> getWorkerContext().createTargetComposer(fieldIdentity));
        }

        public InstanceClassProtocol.Send createInstanceClassProtocol(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress, InstanceClassProtocol>createReceiver(InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL)
                    .createSend(() -> getWorkerContext().createTargetComposer(instanceClassIdentity));
        }

        public InstanceClassProtocol.Send createInstanceClassProtocolForward(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress, InstanceClassProtocol>createReceiver(InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL)
                    .createSend(() -> getWorkerContext().createForwardComposer(instanceClassIdentity));
        }

        public RelationClassProtocol.Send createRelationClassProtocolSend(RelationClass.RelationClassIdentity relationClassIdentity) {
            return getWorkerContext()
                    .<Protocol.ProtocolAddress, RelationClassProtocol>createReceiver(RelationClassProtocol.RELATION_CLASS_PROTOCOL)
                    .createSend(() -> getWorkerContext().createTargetComposer(relationClassIdentity));
        }
    }

    private static List<InstanceIdentity> getRelations(RelationClass.RelationClassIdentity relationClassIdentity, InstanceData instanceData) throws UserException {
        if (relationClassIdentity==null)
            throw new NullPointerException("relationClassIdentity is null!");
        final Optional<RelationClass.RelationDirection> direction = getDirection(relationClassIdentity, instanceData.getIdentity());
        direction.orElseThrow(() -> new UserException(ErrorClass.INSTANCE, ErrorKind.RELATION_NOT_FOUND, MessageValue.address(relationClassIdentity)));
        return instanceData.getRelations()
                .filter(findRelation(relationClassIdentity))
                .map(relationIdentity -> {
                    return relationIdentity.getInstanceIdentity(direction.get());
                })
                .collect(Collectors.toList());
    }

    private static FieldValue.FieldValueIdentity getFieldValueIdentity(Field.FieldIdentity fieldIdentity, InstanceData instanceData) throws UserException {
        if (fieldIdentity==null)
            throw new NullPointerException("fieldIdentity is null!");
        return instanceData.getFieldValues()
                .filter(findField(fieldIdentity))
                .findAny()
                .orElseThrow(() -> new UserException(ErrorClass.INSTANCE, ErrorKind.FIELD_NOT_FOUND, MessageValue.address(Field.FIELD_IDENTITY, fieldIdentity)));
    }
}
