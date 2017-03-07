package org.objectagon.core.rest2.service;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.rest2.service.locator.RestPathImpl;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.service.*;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.ActionTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-01-09.
 */
public class RestService extends AbstractService<RestService.RestServiceWorker, Service.ServiceName> {

    enum RestServiceTaksName implements Task.TaskName {
        INITIALIZE_LOCATOR;
    }

    public static ServiceName REST_SERVICE_NAME = StandardServiceName.name("REST_SERVICE_NAME");
    public static final Name REST_SERVICE_CONFIGURATION_NAME = StandardName.name("REST_SERVICE_CONFIGURATION_NAME");

    public static void registerAtServer(Server server) {
        server.registerFactory(REST_SERVICE_NAME, RestService::new);
    }

    private RestServiceActionLocator restServiceActionLocator = new RestServiceActionLocatorImpl();

    public RestService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        setServiceName(REST_SERVICE_NAME);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        RestServiceConfig restServiceConfig = FindNamedConfiguration.finder(configurations).getConfigurationByName(REST_SERVICE_CONFIGURATION_NAME);
        restServiceActionLocator = restServiceConfig.createRestServiceActionLocator();
    }

    @Override
    protected Optional<TaskBuilder.Builder> internalCreateStartServiceTask(RestServiceWorker serviceWorker) {
        TaskBuilder taskBuilder = serviceWorker.getTaskBuilder();
        final TaskBuilder.Builder<ActionTask> action = taskBuilder.action(RestServiceTaksName.INITIALIZE_LOCATOR, () -> {
            System.out.println("RestService.internalCreateStartServiceTask 1");
            restServiceActionLocator.configure(name -> (ServiceName) getReceiverCtrl().lookupAddressByAlias(name)
                    .orElseThrow(() -> new SevereError(ErrorClass.UNKNOWN, ErrorKind.MISSING_CONFIGURATION)));
            System.out.println("RestService.internalCreateStartServiceTask 2");
        });
        return Optional.of(action);
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
                patternBuilder -> patternBuilder.setMessageNameTrigger(RestServiceProtocol.MessageName.SIMPLE_REST_CONTENT),
                (initializer, context) -> new ProcessRestRequestAction((RestService) initializer, (RestService.RestServiceWorker) context)
        );
    }

    @Override
    protected RestServiceWorker createWorker(WorkerContext workerContext) {
        return new RestServiceWorker(workerContext);
    }

    public class RestServiceWorker extends ServiceWorkerImpl implements RestServiceActionLocator.IdentityLookup {

        public RestServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public Task createTaskFromMethodAndPath(RestServiceProtocol.Method method, Name path, String content, List<KeyValue<ParamName, Message.Value>> params) throws Exception {
            final RestServiceActionLocator.RestPath restPath = RestPathImpl.create(path, this);
            final RestServiceActionLocator.RestAction restAction = restServiceActionLocator.locate(null, method, restPath);
            return restAction.createTask(getWorkerContext().getTaskBuilder(), restPath, params, content);
        }

        @Override
        public Optional<Identity> find(String identityAlias) {
            return getReceiverCtrl().lookupAddressByAlias(StandardName.name(identityAlias)).map(address -> (Identity) address);
        }
    }

    private class ProcessRestRequestAction extends AsyncAction<RestService,RestServiceWorker> {

        RestServiceProtocol.Method method;
        Name path;
        List<KeyValue<ParamName, Message.Value>> params;
        String content;

        public ProcessRestRequestAction(RestService initializer, RestServiceWorker context) {
            super(initializer, context);
        }

        @Override
        public boolean initialize() throws UserException {
            this.method = context.getValue(RestServiceProtocol.METHOD_FIELD).asName();
            this.path = context.getValue(RestServiceProtocol.PATH_FIELD).asName();
            this.content = context.getValue(RestServiceProtocol.CONTENT_FIELD).asText();
            this.params = context.getValue(RestServiceProtocol.PARAMS_FIELD).asMap().entrySet().stream()
                    .map(entry -> KeyValueUtil.createKeyValue( (ParamName) entry.getKey(), (Message.Value) entry.getValue()))
                    .collect(Collectors.toList());
            return super.initialize();
        }

        @Override
        protected Task internalRun(RestServiceWorker actionContext) throws UserException {
            Task task = null;
            try {
                task = context.createTaskFromMethodAndPath(method, path, content, params);
            } catch (Exception e) {
                e.printStackTrace(); //TODO // FIXME: 2017-02-26
                throw new UserException(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED);
            }
            return task;
        }
    }

    public interface RestServiceConfig extends NamedConfiguration {
        RestServiceActionLocator createRestServiceActionLocator();
    }


}
