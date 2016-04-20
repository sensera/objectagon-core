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
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.EntityProtocol;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;

import static org.objectagon.core.object.fieldvalue.FieldValueUtil.findField;
import static org.objectagon.core.storage.entity.EntityService.EXTRA_ADDRESS_CONFIG_NAME;


/**
 * Created by christian on 2015-10-20.
 */
public class InstanceImpl extends EntityImpl<Instance.InstanceIdentity,Instance.InstanceData,StandardVersion,InstanceImpl.InstanceWorker> implements Instance {

    public InstanceImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, Instance.DATA_TYPE);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected InstanceData createNewData() {
        return InstanceDataImpl.create(getAddress(), StandardVersion.create(0L));
    }

    @Override
    protected Instance.InstanceData upgrade(Instance.InstanceData data, DataVersion<InstanceIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data; //TODO improve
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
    }

    @Override
    protected Instance.InstanceIdentity createAddress(Configurations... configurations) {
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
                .orElse(w -> super.handle(w));
    }

    private Optional<Task> dropFieldValues(InstanceWorker instanceWorker, Instance.InstanceData instanceData) {
        return Optional.of(instanceWorker.getTaskBuilder()
                .message(
                    InstanceProtocol.MessageName.DROP_FIELD_VALUES,
                    FieldValueProtocol.FIELD_VALUE_PROTOCOL,
                    instanceWorker.getValue(Field.FIELD_IDENTITY).<Field.FieldIdentity>asAddress(),
                    EntityProtocol.Send::delete)
                .create()
        );
    }

    private void addField(InstanceWorker instanceWorker, Instance.InstanceData instanceData, Instance.InstanceDataChange change, Message.Values values) {
        change.addField(MessageValueFieldUtil.create(values).getValueByField(Field.FIELD_IDENTITY).asAddress());
    }

    private void removeField(InstanceWorker instanceWorker, Instance.InstanceData instanceData, Instance.InstanceDataChange change, Message.Values values) {
        change.removeField(MessageValueFieldUtil.create(values).getValueByField(Field.FIELD_IDENTITY).asAddress());
    }

    private void getValue(InstanceWorker instanceWorker, Instance.InstanceData instanceData) throws UserException {
        System.out.println("InstanceImpl.getValue <"+instanceWorker.currentTransaction()+">");
        try {
            FieldValue.FieldValueIdentity fieldValueIdentity = getFieldValueIdentity(
                    instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress(),
                    instanceData);
            instanceWorker.createFieldValueProtocolForward(fieldValueIdentity)
                    .getValue();
        } catch (UserException e) {
            if (ErrorKind.FIELD_NOT_FOUND.equals(e.getErrorKind())) {
                System.out.println("InstanceImpl.getValue NOT_FOUND will get default value");
                instanceWorker.createFieldProtocolForward(instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress()).getDefaultValue();
            } else
                throw e;
        }
    }

    private Optional<Task> createValue(InstanceWorker instanceWorker, Instance.InstanceData instanceData) {
        Message.Value value = instanceWorker.getValue(FieldValue.VALUE).asValues().values().iterator().next();
        System.out.println("InstanceImpl.createValue "+value+" <"+instanceWorker.currentTransaction()+">");
        try {
            FieldValue.FieldValueIdentity fieldValueIdentity = getFieldValueIdentity(
                    instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress(),
                    instanceData);
            instanceWorker.createFieldValueProtocolForward(fieldValueIdentity)
                    .setValue(value);
            instanceWorker.replyOk();
        } catch (UserException e) {
            if (ErrorKind.FIELD_NOT_FOUND.equals(e.getErrorKind())) {
                return Optional.of(instanceWorker.createFieldProtocolSend(instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress())
                        .createFieldValue(getAddress(), value));
            } else
                instanceWorker.replyWithError(e);
        }
        return Optional.empty();
    }

    private void setValue(InstanceWorker instanceWorker, Instance.InstanceData instanceData, Instance.InstanceDataChange change, Message.Values values) {
        FieldValue.FieldValueIdentity fieldValueIdentity = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
        System.out.println("InstanceImpl.setValue "+fieldValueIdentity+" <"+instanceWorker.currentTransaction()+">");
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
    }

    private static FieldValue.FieldValueIdentity getFieldValueIdentity(Field.FieldIdentity fieldIdentity, Instance.InstanceData instanceData) throws UserException {
        if (fieldIdentity==null)
            throw new NullPointerException("fieldIdentity is null!");
        return instanceData.getFieldValues()
                .filter(findField(fieldIdentity))
                .findAny()
                .orElseThrow(() -> new UserException(ErrorClass.INSTANCE, ErrorKind.FIELD_NOT_FOUND, MessageValue.address(Field.FIELD_IDENTITY, fieldIdentity)));
    }

}
