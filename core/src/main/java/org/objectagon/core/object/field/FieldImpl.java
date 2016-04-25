package org.objectagon.core.object.field;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.field.data.FieldDataImpl;
import org.objectagon.core.object.fieldvalue.FieldValueService;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;

import static org.objectagon.core.storage.entity.EntityService.EXTRA_ADDRESS_CONFIG_NAME;

/**
 * Created by christian on 2016-03-04.
 */
public class FieldImpl extends EntityImpl<Field.FieldIdentity, Field.FieldData, StandardVersion, FieldImpl.FieldEntityWorker> implements Field {

    public FieldImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, Field.DATA_TYPE);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected FieldData createNewData(Optional<StandardVersion> version) {
        return FieldDataImpl.create(getAddress(),version.orElse(StandardVersion.create(0L)), null, StandardFieldType.Text);
    }

    @Override
    protected FieldEntityWorker createWorker(Receiver.WorkerContext workerContext) {
        return new FieldEntityWorker(workerContext);
    }

    @Override
    protected FieldIdentity createAddress(Configurations... configurations) {
        FindNamedConfiguration finder = FindNamedConfiguration.finder(configurations);
        FieldProtocol.ConfigField configField = finder.getConfigurationByName(EXTRA_ADDRESS_CONFIG_NAME);
        return finder.createConfiguredAddress((serverId, timestamp, id) -> new FieldIdentityImpl(configField.getInstanceClassIdentity(), serverId, timestamp, id));
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
                .trigger(FieldProtocol.MessageName.GET_DEFAULT_VALUE, read(this::getDefaultValue))
                .trigger(FieldProtocol.MessageName.GET_DEFAULT_VALUE_FOR_INSTANCE, read(this::getDefaultValueForInstance))
                .trigger(FieldProtocol.MessageName.SET_DEFAULT_VALUE, write(this::<FieldDataChange>setDefaultValue))
                .trigger(FieldProtocol.MessageName.CREATE_FIELD_VALUE, read(this::createFieldValue))
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

    private void getDefaultValueForInstance(FieldEntityWorker fieldEntityWorker, FieldData fieldData) {
        if (fieldData.getDefaultValue().isPresent())
            System.out.println("FieldImpl.getDefaultValueForInstance value="+fieldData.getDefaultValue().get());
        else
            System.out.println("FieldImpl.getDefaultValueForInstance value=NO VALUE");
        fieldEntityWorker.replyWithParam(MessageValue.values(FieldValue.VALUE, fieldData.getDefaultValue().orElse(MessageValue.empty())));
    }

    private void getDefaultValue(FieldEntityWorker fieldEntityWorker, FieldData fieldData) {
        fieldEntityWorker.replyWithParam(MessageValue.values(Field.DEFAULT_VALUE, fieldData.getDefaultValue().orElse(MessageValue.empty())));
    }

    private void setDefaultValue(FieldEntityWorker fieldEntityWorker, FieldData fieldData, FieldDataChange fieldIdentityStandardVersionChange, Message.Values values) {
        Message.Value valueByField = MessageValueFieldUtil.create(values).getValueByField(DEFAULT_VALUE).asValues().values().iterator().next();
        fieldIdentityStandardVersionChange.setDefaultValue(valueByField);
    }

    private void createFieldValue(FieldEntityWorker fieldEntityWorker, FieldData fieldData) {
        //TODO improve this
        Task task = fieldEntityWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, FieldValueService.NAME)
                .create(fieldEntityWorker.getValue(FieldValue.VALUE),
                        fieldEntityWorker.getValue(Instance.INSTANCE_IDENTITY),
                        MessageValue.address(Field.FIELD_IDENTITY, getAddress()));
        task.addFailedAction(fieldEntityWorker::failed);
        task.addSuccessAction((messageName, values) -> {
            FieldValue.FieldValueIdentity fieldValueIdentity = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
            Message.Value initialValue = fieldEntityWorker.getValue(FieldValue.VALUE).asValues().values().iterator().next();
            Task nextTask = fieldEntityWorker.createFieldValueProtocolSend(fieldValueIdentity).setValue(initialValue);
            nextTask.addFailedAction(fieldEntityWorker::failed);
            nextTask.addSuccessAction((messageName1, values1) -> fieldEntityWorker.success(StandardProtocol.MessageName.OK_MESSAGE, values));
            nextTask.start();
        });
        task.start();
    }
}
