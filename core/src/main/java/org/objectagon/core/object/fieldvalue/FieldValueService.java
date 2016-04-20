package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.*;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-03-07.
 */
public class FieldValueService extends EntityService<Service.ServiceName, FieldValue.FieldValueIdentity, FieldValue.FieldValueData, EntityService.EntityServiceWorker> {

    public static ServiceName NAME = StandardServiceName.name("FIELD_VALUE_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, FieldValueService::new);
    }

    public FieldValueService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, FieldValue.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {return FieldValueImpl::new;}

    @Override
    protected DataVersion<FieldValue.FieldValueIdentity, StandardVersion> createInitialDataFromValues(FieldValue.FieldValueIdentity identity, Message.Values initialParams) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0L), 0L, StandardVersion::new);
    }

    @Override
    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of(new FieldValueProtocol.ConfigFieldValue(){
            @Override public Field.FieldIdentity getFieldIdentity() {
                return messageValueFieldUtil.getValueByFieldOption(Field.FIELD_IDENTITY)
                        .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(Field.FIELD_IDENTITY,"UNKNOWN")))
                        .asAddress();
            }

            @Override public Instance.InstanceIdentity getInstanceIdentity() {
                return messageValueFieldUtil.getValueByFieldOption(Instance.INSTANCE_IDENTITY)
                        .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(Instance.INSTANCE_IDENTITY,"UNKNOWN")))
                        .asAddress();
            }
        });
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

}
