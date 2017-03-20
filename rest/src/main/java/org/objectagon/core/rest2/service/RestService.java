package org.objectagon.core.rest2.service;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.rest2.service.locator.RestPathImpl;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.service.*;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.task.ActionTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-01-09.
 */
public class RestService extends AbstractService<RestService.RestServiceWorker, Service.ServiceName>  {

    private final RestSessionToken STATIC_REST_TOKEN = RestSessionToken.name("1234567890");

    enum RestServiceTaksName implements Task.TaskName {
        INITIALIZE_LOCATOR;
    }

    private Map<RestSessionToken, RestServiceActionLocator.RestSession> sessions = new HashMap<>();

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

    public class RestServiceWorker extends ServiceWorkerImpl  {

        public RestServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }

        public Task createTaskFromMethodAndPath(RestServiceProtocol.Method method, Name path, String content, List<KeyValue<ParamName, Message.Value>> params, RestSessionToken token) throws UserException {
            final RestServiceActionLocator.RestSession restSession = getRestSession(token);
            final RestServiceActionLocator.RestPath restPath = RestPathImpl.create(path, restSession);
            final RestServiceActionLocator.RestAction restAction = restServiceActionLocator.locate(null, method, restPath);
            final TaskBuilder taskBuilder = getWorkerContext().getTaskBuilder();
            taskBuilder.addHeader(MessageValue.name(RestServiceProtocol.TOKEN_HEADER, restSession.geRestSessionToken()));
            restSession.useActiveTransaction(Transaction.addAsHeader(taskBuilder));
            return restAction.createTask(taskBuilder, new RestServiceActionLocator.IdentityStore() {
                @Override public void updateIdentity(Identity identity, String identityAlias) {
                    restSession.updateAlias(identity, identityAlias);
                }
                @Override public void updateSessionTransaction(Transaction transaction) {
                    restSession.updateTransaction(transaction);
                }
                @Override public Optional<Transaction> getActiveTransaction() {
                    return restSession.getActiveTransaction();
                }
            }, restPath, params, content);
        }

        private RestServiceActionLocator.RestSession getRestSession(RestSessionToken token) {
            RestServiceActionLocator.RestSession restSession = sessions.get(token);
            if (restSession==null && token != null) {
                if (token==null)
                    throw new NullPointerException("token is null!");
                if (!Objects.equals(STATIC_REST_TOKEN,token))
                    throw new NullPointerException("restSession is null!");
                createNewToken();
                restSession = sessions.get(token);
            }
            return restSession;
        }

        public RestSessionToken getToken() {
            return getWorkerContext().getHeader(RestServiceProtocol.TOKEN_HEADER)
                    .getOptionalValue()
                    .map(value -> RestSessionToken.name( (String) value))
                    .orElseGet(() -> createNewToken());
        }
    }

    private RestSessionToken createNewToken() {
        //final RestSessionToken name = RestSessionToken.name(Long.toHexString(System.currentTimeMillis()));
        final RestSessionToken name = STATIC_REST_TOKEN;
        sessions.put(name, new RestSessionImpl(name));
        System.out.println("RestService.createNewToken "+name);
        return name;
    }

    private class ProcessRestRequestAction extends AsyncAction<RestService,RestServiceWorker> {

        RestServiceProtocol.Method method;
        Name path;
        List<KeyValue<ParamName, Message.Value>> params;
        String content;
        RestSessionToken token;

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
            this.token = context.getToken();
            return super.initialize();
        }

        @Override
        protected Task internalRun(RestServiceWorker actionContext) throws UserException {
            return context.createTaskFromMethodAndPath(method, path, content, params, token);
        }
    }

    public interface RestServiceConfig extends NamedConfiguration {
        RestServiceActionLocator createRestServiceActionLocator();
    }

    public static class RestSessionImpl implements RestServiceActionLocator.RestSession {

        private Map<String,Identity> aliases = new HashMap<>();
        private RestSessionToken name;
        private Transaction transaction;

        @Override
        public RestSessionToken geRestSessionToken() {
            return name;
        }

        @Override
        public void updateTransaction(Transaction transaction) {
            this.transaction = transaction;
            System.out.println("RestSessionImpl.updateTransaction !!!!!!!!!!!!!!!! "+transaction);
        }

        @Override
        public Optional<Transaction> getActiveTransaction() {
            return Optional.of(transaction);
        }

        @Override
        public void useActiveTransaction(Consumer<Transaction> consumer) {
            if (transaction!=null) {
                consumer.accept(transaction);
            }
            System.out.println("RestSessionImpl.useActiveTransaction !!!!!!! "+transaction);
        }

        public RestSessionImpl(RestSessionToken name) {
            this.name = name;
        }

        @Override
        public void updateAlias(Identity identity, String alias) {
            System.out.println("!!!!!!!!!!!!!!!!!! RestSessionImpl.updateAlias "+alias);
            aliases.put(alias, identity);
        }

        @Override
        public Optional<Identity> getIdentityByAlias(String alias) {
            System.out.println("RestSessionImpl.getIdentityByAlias "+alias);
            return Optional.ofNullable(aliases.get(alias));
        }
    }

}
