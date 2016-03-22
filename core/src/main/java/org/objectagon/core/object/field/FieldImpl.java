package org.objectagon.core.object.field;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.object.field.data.FieldDataImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-03-04.
 */
public class FieldImpl extends EntityImpl<Field.FieldIdentity, Field.FieldData, StandardVersion, FieldImpl.FieldEntityWorker> implements Field {

    public FieldImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected FieldData createNewData() {
        return FieldDataImpl.create(getAddress(),StandardVersion.create(0L), null, StandardFieldType.Text);
    }

    @Override
    protected FieldEntityWorker createWorker(Receiver.WorkerContext workerContext) {
        return new FieldEntityWorker(workerContext);
    }

    @Override
    protected FieldIdentity createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<FieldIdentity> initializer) {
        FieldProtocol.ConfigField configField = (FieldProtocol.ConfigField) initializer.<FieldProtocol.ConfigField>initialize(getAddress());
        return new FieldIdentityImpl(configField.getInstanceClassIdentity(), serverId, timestamp, id);
    }

    public class FieldEntityWorker extends EntityWorkerImpl {
        public FieldEntityWorker(Receiver.WorkerContext workerContext) {
            super(workerContext);
        }
    }

    @Override
    protected FieldData upgrade(FieldData data, DataVersion<FieldIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data; //TODO this needs to be finished!
    }

    @Override
    protected void handle(FieldEntityWorker worker) {
        triggerBuilder(worker)
                .trigger(FieldProtocol.MessageName.GET_NAME, read(this::getName))
                .trigger(FieldProtocol.MessageName.SET_NAME, write(this::<FieldDataChange>setName))
                .trigger(FieldProtocol.MessageName.GET_TYPE, read(this::getType))
                .trigger(FieldProtocol.MessageName.SET_TYPE, write(this::<FieldDataChange>setType))
                .orElse(w -> super.handle(w));
    }

    private void getName(FieldEntityWorker fieldEntityWorker, FieldData fieldData) {
        fieldEntityWorker.replyWithParam(MessageValue.name(FIELD_NAME, fieldData.getName()));
    }

    private void setName(FieldEntityWorker fieldEntityWorker, FieldData fieldData, FieldDataChange fieldIdentityStandardVersionChange, Message.Values values) {
        fieldIdentityStandardVersionChange.setName(MessageValueFieldUtil.create(values).getValueByField(FIELD_NAME).asName());
    }

    private void getType(FieldEntityWorker fieldEntityWorker, FieldData fieldData) {
        fieldEntityWorker.replyWithParam(MessageValue.name(FIELD_TYPE, fieldData.getType()));
    }

    private void setType(FieldEntityWorker fieldEntityWorker, FieldData fieldData, FieldDataChange fieldIdentityStandardVersionChange, Message.Values values) {
        fieldIdentityStandardVersionChange.setType(MessageValueFieldUtil.create(values).getValueByField(FIELD_TYPE).asName());
    }
}
