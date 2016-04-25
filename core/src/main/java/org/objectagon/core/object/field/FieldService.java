package org.objectagon.core.object.field;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.DataVersionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-03-06.
 */
public class FieldService extends EntityService<Service.ServiceName, Field.FieldIdentity, Field.FieldData, EntityService.EntityServiceWorker> {

    public static ServiceName NAME = StandardServiceName.name("FIELD_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, FieldService::new);
    }

    public FieldService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, Field.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {return FieldImpl::new;}

    @Override
    protected DataVersion<Field.FieldIdentity, StandardVersion> createInitialDataFromValues(Field.FieldIdentity identity, Message.Values initialParams, Transaction transaction) {
        return new DataVersionImpl<>(identity, StandardVersion.create(0L), 0L, StandardVersion::new);
    }

    @Override
    public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of((FieldProtocol.ConfigField) () -> messageValueFieldUtil.getValueByFieldOption(InstanceClass.INSTANCE_CLASS_IDENTITY)
                .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(InstanceClass.INSTANCE_CLASS_IDENTITY,"UNKNOWN")))
                .asAddress()
        );
    }

    @Override
    protected EntityServiceWorker createWorker(WorkerContext workerContext) {
        return new EntityServiceWorker(workerContext);
    }

}
