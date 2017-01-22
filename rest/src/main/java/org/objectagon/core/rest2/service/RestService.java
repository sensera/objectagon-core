package org.objectagon.core.rest2.service;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.service.*;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 2017-01-09.
 */
public class RestService extends AbstractService<RestService.RestServiceWorker, Service.ServiceName> {

    public static ServiceName REST_SERVICE_NAME = StandardServiceName.name("REST_SERVICE_NAME");

    public static void registerAtServer(Server server) {
        server.registerFactory(REST_SERVICE_NAME, RestService::new);
    }

    private Map<Name,AddressList<Address>> eventListeners = new HashMap<>();

    public RestService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        setServiceName(REST_SERVICE_NAME);
    }

    @Override
    protected Service.ServiceName createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, addressId) ->
                StandardServiceNameAddress.name(REST_SERVICE_NAME, serverId, timestamp, addressId)
        );
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(RestServiceProtocol.MessageName.REST_REQUEST),
                (initializer, context) -> new ProcessRestRequestAction((RestService) initializer, (RestService.RestServiceWorker) context)
        );
    }

    @Override
    protected RestServiceWorker createWorker(WorkerContext workerContext) {
        return new RestServiceWorker(workerContext);
    }

    public class RestServiceWorker extends ServiceWorkerImpl {

        public RestServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public Task getTaskFromMethodAndPath(RestServiceProtocol.Method method, Name path) throws UserException {
            throw new UserException(null, null);
        }
    }

    private class ProcessRestRequestAction extends AsyncAction<RestService,RestServiceWorker> {

        RestServiceProtocol.Method method;
        Name path;
        List<KeyValue<ParamName, Message.Value>> params;
        Task task;

        public ProcessRestRequestAction(RestService initializer, RestServiceWorker context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            this.method = context.getValue(RestServiceProtocol.METHOD_FIELD).asName();
            this.path = context.getValue(RestServiceProtocol.PATH_FIELD).asName();
            this.params = context.getValue(RestServiceProtocol.PATH_FIELD).asName();
            this.task = context.getTaskFromMethodAndPath(method, path);
            return super.initialize();
        }

        @Override
        protected Task internalRun(RestServiceWorker actionContext) throws UserException {
            return task;
        }
    }


}
