package org.objectagon.core.object.instance;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
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

import java.util.Optional;

import static org.objectagon.core.object.fieldvalue.FieldValueUtil.findField;


/**
 * Created by christian on 2015-10-20.
 */
public class InstanceImpl extends EntityImpl<Instance.InstanceIdentity,Instance.InstanceData,StandardVersion,InstanceImpl.InstanceWorker> implements Instance {

    public InstanceImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
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
    protected Instance.InstanceIdentity createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<Instance.InstanceIdentity> initializer) {
        InstanceProtocol.ConfigInstance configInstance = (InstanceProtocol.ConfigInstance) initializer.<InstanceProtocol.ConfigInstance>initialize(getAddress());
        return new InstanceIdentityImpl(configInstance.getInstanceClassIdentity(), serverId, timestamp, id);
    }

    @Override
    protected void handle(InstanceWorker worker) {
        triggerBuilder(worker)
                .trigger(InstanceProtocol.MessageName.GET_VALUE, read(this::getValue))
                .trigger(InstanceProtocol.MessageName.SET_VALUE, read(this::setValue))

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
                    EntityProtocol.Send::remove)
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
        FieldValue.FieldValueIdentity fieldValueIdentity = getFieldValueIdentity(instanceWorker, instanceData);
        instanceWorker.createFieldValueProtocolForward(fieldValueIdentity)
                .getValue();
    }

    private void setValue(InstanceWorker instanceWorker, Instance.InstanceData instanceData) throws UserException {
        FieldValue.FieldValueIdentity fieldValueIdentity = getFieldValueIdentity(instanceWorker, instanceData);
        instanceWorker.createFieldValueProtocolForward(fieldValueIdentity)
                .setValue(instanceWorker.getValue(FieldValue.VALUE));
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
                    .<Protocol.ProtocolAddress, FieldValueProtocol>createReceiver(FieldValueProtocol.FIELD_VALUE_PROTOCOL, null)
                    .createForward(() -> getWorkerContext().createForwardComposer(fieldValueIdentity));
        }
    }

    private static FieldValue.FieldValueIdentity getFieldValueIdentity(InstanceWorker instanceWorker, Instance.InstanceData instanceData) throws UserException {
        Field.FieldIdentity fieldIdentity = instanceWorker.getValue(Field.FIELD_IDENTITY).asAddress();
        return instanceData.getFieldValues()
                .filter(findField(fieldIdentity))
                .findAny()
                .orElseThrow(() -> new UserException(ErrorClass.INSTANCE, ErrorKind.FIELD_NOT_FOUND));
    }

}
