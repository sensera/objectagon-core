package org.objectagon.core.rest;

import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;

/**
 * Created by christian on 2016-02-11.
 */
public class RestService extends AbstractService<RestService.RestServiceWorker, StandardAddress> {

    public static final Name REST_SERVICE_NAME = StandardName.name("REST_SERVICE_NAME");

    public void registerAt(Server server) {
        server.registerFactory(REST_SERVICE_NAME, RestService::new);
    }

    public RestService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected RestServiceWorker createWorker(WorkerContext workerContext) {
        return new RestServiceWorker(workerContext);
    }

    @Override
    protected StandardAddress createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardAddress::standard);
    }

    public class RestServiceWorker extends ServiceWorkerImpl {
        public RestServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(RestServiceProtocol.MessageName.HTTP_SIMPLE_CALL),
                (initializer, context) -> new HttpSimpleCallAction((RestService) initializer, (RestServiceWorker) context)
        );
    }

    private static class HttpSimpleCallAction extends StandardAction<RestService, RestServiceWorker> {

        RestServiceProtocol.MethodType methodType;
        String url;
        Message.Values headers;

        public HttpSimpleCallAction(RestService initializer, RestServiceWorker context) {
            super(initializer, context);
        }

        public boolean initialize() {
            methodType = context.getValue(RestServiceProtocol.METHOD_TYPE).asName();
            url = context.getValue(RestServiceProtocol.URL).asText();
            headers = context.getValue(RestServiceProtocol.HEADERS).asValues();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            //initializer.stopListenTo(name, address);
            return Optional.empty();
        }
    }
}
