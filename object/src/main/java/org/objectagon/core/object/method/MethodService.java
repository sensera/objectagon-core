package org.objectagon.core.object.method;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.Meta;
import org.objectagon.core.object.Method;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.StandardServiceName;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.DataRevisionImpl;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-02-28.
 */
public class MethodService extends EntityService<Service.ServiceName, Method.MethodIdentity, Method.MethodData, EntityService.EntityServiceWorker>  {

    public static ServiceName NAME = StandardServiceName.name("METHOD_SERVICE");

    public static void registerAt(Server server) {
        server.registerFactory(NAME, MethodService::new);
    }

    public MethodService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, NAME, Method.DATA_TYPE);
    }

    @Override protected Server.Factory createEntityFactory() {return MethodImpl::new;}
    @Override protected DataRevision<Method.MethodIdentity, StandardVersion> createInitialDataFromValues(Method.MethodIdentity identity, Message.Values initialParams, Transaction transaction) {
        return DataRevisionImpl.create(identity, StandardVersion.create(0L), 0L, StandardVersion::new);
    }

    @Override protected EntityServiceWorker createWorker(WorkerContext workerContext) {return new MethodServiceWorker(workerContext);}

    private class MethodServiceWorker extends EntityServiceWorker {
        public MethodServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public Optional<Task> find(Name name) {
            return Optional.of(nameSearch(name));
        }
    }

    @Override public Optional<NamedConfiguration> extraAddressCreateConfiguration(MessageValueFieldUtil messageValueFieldUtil) {
        return Optional.of((Method.ConfigMethod) () -> messageValueFieldUtil.getValueByFieldOption(Meta.META_IDENTITY)
                                   .orElseThrow(() -> new SevereError(ErrorClass.ENTITY_SERVICE, ErrorKind.MISSING_CONFIGURATION, MessageValue.text(InstanceClass.INSTANCE_CLASS_IDENTITY, "UNKNOWN")))
                                   .asAddress()
                          );
    }

}
