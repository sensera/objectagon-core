package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.FieldValueProtocol;
import org.objectagon.core.object.fieldvalue.data.FieldValueDataImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;

import static org.objectagon.core.storage.entity.EntityService.EXTRA_ADDRESS_CONFIG_NAME;

/**
 * Created by christian on 2016-03-04.
 */
public class FieldValueImpl extends EntityImpl<FieldValue.FieldValueIdentity, FieldValue.FieldValueData, StandardVersion, FieldValueImpl.FieldValueWorker> implements FieldValue {

    public FieldValueImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, FieldValue.DATA_TYPE);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected FieldValueData createNewData(Optional<StandardVersion> version) {
        return FieldValueDataImpl.create(getAddress(), version.orElse(StandardVersion.create(0L)), MessageValue.empty());
    }

    @Override
    protected FieldValueData upgrade(FieldValueData data, DataVersion<FieldValueIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data.<FieldValue.ChangeFieldValue>change().setValue(MessageValue.text(null,"Hubbe liten")).create(null); //TODO this needs to be finished!
    }

    @Override
    protected FieldValueWorker createWorker(WorkerContext workerContext) {
        return new FieldValueWorker(workerContext);
    }

    @Override
    protected FieldValueIdentity createAddress(Configurations... configurations) {
        FindNamedConfiguration finder = FindNamedConfiguration.finder(configurations);
        FieldValueProtocol.ConfigFieldValue configFieldValue = finder.getConfigurationByName(EXTRA_ADDRESS_CONFIG_NAME);
        return finder.createConfiguredAddress((serverId, timestamp, id) ->
                new FieldValueIdentityImpl(configFieldValue.getInstanceIdentity(), configFieldValue.getFieldIdentity(), serverId, timestamp, id));
    }

    public class FieldValueWorker extends EntityWorkerImpl {
        public FieldValueWorker(WorkerContext workerContext) {
            super(workerContext);
        }

    }

    @Override
    protected void handle(FieldValueWorker worker) {
        triggerBuilder(worker)
                .trigger(FieldValueProtocol.MessageName.GET_VALUE, read(this::getValue))
                .trigger(FieldValueProtocol.MessageName.SET_VALUE, write(this::<ChangeFieldValue>setValue))
                .trigger(FieldValueProtocol.MessageName.ADD_VALUE, this::addValue)
                .trigger(FieldValueProtocol.MessageName.REMOVE_VALUE, this::removeValue)
                .orElse(w -> super.handle(w));
    }

    public void getValue(FieldValueWorker fieldValueWorker, FieldValueData data) {
        //System.out.println("FieldValueImpl.getValue "+data.getValue()+" <"+fieldValueWorker.currentTransaction()+">");
        fieldValueWorker.replyWithParam(MessageValue.values(FieldValue.VALUE, data.getValue()));
    }

    private void setValue(FieldValueWorker fieldValueWorker, FieldValueData fieldValueData, ChangeFieldValue fieldValueIdentityStandardVersionChange, Message.Values preparedValues) {
        Message.Value value = fieldValueWorker.getValue(FieldValue.VALUE).asValues().values().iterator().next();
        //System.out.println("FieldValueImpl.setValue "+value+" <"+fieldValueWorker.currentTransaction()+">");
        fieldValueIdentityStandardVersionChange.setValue(value);
    }

    private void addValue(FieldValueWorker fieldValueWorker) {

    }

    private void removeValue(FieldValueWorker fieldValueWorker) {

    }



}
