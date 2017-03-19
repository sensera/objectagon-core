package org.objectagon.core.rest2.http;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.service.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-01-22.
 */
public class HttpService extends AbstractService<HttpService.HttpServiceWorker, Service.ServiceName> {

    public static final Name HTTP_SERVICE_CONFIGURATION_NAME = StandardName.name("HTTP_SERVICE_CONFIGURATION_NAME");
    public static final ServiceName HTTP_SERVICE_NAME = StandardServiceName.name("HTTP_SERVICE_NAME");

    public static void registerAtServer(Server server) {
        server.registerFactory(HTTP_SERVICE_NAME, HttpService::new);
    }

    enum HttpServiceTaskName implements Task.TaskName { StartHttpServer, StopHttpServer, SendHttpMessage}

    private HttpServer httpServer;
    private Address restServiceAddress = null;
    private CreateHttpServer createHttpServer;
    private int port;
    private boolean simpleCommunication = true;

    public HttpService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        setServiceName(HTTP_SERVICE_NAME);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        HttpServiceConfig httpServiceConfig = FindNamedConfiguration.finder(configurations).getConfigurationByName(HTTP_SERVICE_CONFIGURATION_NAME);
        port = httpServiceConfig.getListenPort();
        createHttpServer  = httpServiceConfig.getCreateHttpServer();
        restServiceAddress = httpServiceConfig.getRestServiceAddress();
    }

    @Override
    protected Service.ServiceName createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, addressId) ->
                StandardServiceNameAddress.name(HTTP_SERVICE_NAME, serverId, timestamp, addressId)
        );
    }

    @Override
    protected Optional<TaskBuilder.Builder> internalCreateStartServiceTask(HttpServiceWorker serviceWorker) {
        return Optional.of(serviceWorker.getTaskBuilder().action(HttpServiceTaskName.StartHttpServer, () -> {
            httpServer = createHttpServer.create(new LocalHttpCommunicator(), port);
            try {
                httpServer.start();
                System.out.println("HttpService.internalCreateStartServiceTask started");
            } catch (FailedToStartException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    protected Optional<TaskBuilder.Builder> internalCreateStopServiceTask(HttpServiceWorker serviceWorker) {
        return Optional.of(serviceWorker.getTaskBuilder().action(HttpServiceTaskName.StopHttpServer, () -> {
            if (httpServer == null)
                return;
            try {
                httpServer.stop();
                System.out.println("HttpService.internalCreateStopServiceTask stopped");
            } catch (FailedToStopException e) {
                e.printStackTrace();
            }
            httpServer = null;
        }));
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
    }

    @Override
    protected HttpServiceWorker createWorker(WorkerContext workerContext) {
        return new HttpServiceWorker(workerContext);
    }

    class HttpServiceWorker extends ServiceWorkerImpl {
        HttpServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }

    class LocalHttpCommunicator implements HttpCommunicator {

        private Protocol.CreateSendParam createSendParam;
        private final RestServiceProtocol restServiceProtocol;

        public LocalHttpCommunicator() {
            restServiceProtocol = getReceiverCtrl().createReceiver(RestServiceProtocol.REST_SERVICE_PROTOCOL);
        }

        @Override
        public AsyncContent sendMessageWithAsyncContent(String method, String path, List<HttpParamValues> params, List<HttpParamValues> headers) {
            createSendParam = () -> new StandardComposer(
                    HttpService.this,
                    restServiceAddress,
                    () -> headers.stream().map(HttpParamValues::asValue).collect(Collectors.toList()));
            if (simpleCommunication)
                return new SimpleLocalMessageSession(restServiceProtocol.createSimplifiedSend(createSendParam))
                    .sendRestRequest(method, path, params);
            return new LocalMessageSession(restServiceProtocol.createSend(createSendParam))
                    .sendRestRequest(method, path, params);
        }

    }


    static Function<HttpCommunicator.HttpParamValues, KeyValue<ParamName, Message.Value>> getHttpParamValuesKeyValueFunction() {
        return httpParamValues -> RestServiceProtocol.createParamKeyValue(
                ParamName.create(httpParamValues.getParam()),
                MessageValue.values(httpParamValues.getValues().stream().map(MessageValue::text)));
    }

    public interface HttpServiceConfig extends NamedConfiguration {
        Address getRestServiceAddress();
        int getListenPort();
        CreateHttpServer getCreateHttpServer();
    }

    public interface HttpServer extends StartStopController {}

    @FunctionalInterface
    public interface  CreateHttpServer {
        HttpServer create(HttpCommunicator httpCommunicator, int port);
    }


}
