package org.objectagon.core.rest2.http;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.utils.ReadStream;
import org.objectagon.core.service.*;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FailedToStartException;
import org.objectagon.core.utils.FailedToStopException;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.KeyValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private int port;

    public HttpService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        setServiceName(HTTP_SERVICE_NAME);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        HttpServiceConfig httpServiceConfig = FindNamedConfiguration.finder(configurations).getConfigurationByName(HTTP_SERVICE_CONFIGURATION_NAME);
        port = httpServiceConfig.getListenPort();
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
            httpServer = new HttpServer(new LocalHttpCommunicator(), port);
            try {
                httpServer.start();
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

    public class HttpServiceWorker extends ServiceWorkerImpl {

        public HttpServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }

    class LocalHttpCommunicator implements HttpCommunicator {

        private Protocol.CreateSendParam createSendParam;
        private final RestServiceProtocol restServiceProtocol;

        public LocalHttpCommunicator() {
            restServiceProtocol = getReceiverCtrl().createReceiver(RestServiceProtocol.REST_SERVICE_PROTOCOL);
            createSendParam = () -> new StandardComposer(HttpService.this, restServiceAddress, MessageValue.empty().asValues());
        }

        @Override
        public AsyncContent sendMessageWithAsyncContent(String method, String path, List<HttpParamValues> params) {
            return new LocalMessageSession(restServiceProtocol.createSend(createSendParam))
                    .sendRestRequest(method, path, params);
        }

    }

    class LocalMessageSession implements HttpCommunicator.AsyncContent, Task.SuccessAction, Task.FailedAction, HttpCommunicator.AsyncReply {

        private final RestServiceProtocol.Send send;
        private Consumer<String> consumeReplyContent;
        private Consumer<Exception> failed;
        private long contentSequence = 0;

        public LocalMessageSession(RestServiceProtocol.Send send) {
            this.send = send;
        }

        private LocalMessageSession sendRestRequest(String method, String path, List<HttpCommunicator.HttpParamValues> params) {
            send.restRequest(RestServiceProtocol.getMethodFromString(method),
                    RestServiceProtocol.getPathFromString(path),
                    RestServiceProtocol.getParams(params, getHttpParamValuesKeyValueFunction()))
                 .start();
            return this;
        }

        private synchronized long nextContentSequenceNumber() { return contentSequence++;}

        @Override
        public void pushContent(String content) {
            send.restContent(nextContentSequenceNumber(), content.getBytes()).start();
        }

        @Override
        public void pushContent(InputStream content) {
            try {
                send.restContent(nextContentSequenceNumber(), ReadStream.readStream(content)).start();
            } catch (IOException e) {
                e.printStackTrace();
                //TODO complete failed
                //failed();
            }
        }

        @Override
        public void pushContent(ByteBuf content) {
            byte[] bytes = new byte[content.readableBytes()];
            content.readBytes(bytes);
            send.restContent(nextContentSequenceNumber(), bytes).start();
        }

        @Override
        public HttpCommunicator.AsyncReply completed() {
            send.completed().start();
            return this;
        }

        @Override
        public void receiveReply(Consumer<String> consumeReplyContent, Consumer<Exception> failed) {
            this.consumeReplyContent = consumeReplyContent;
            this.failed = failed;
        }

        @Override
        public void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
            failed.accept(new Exception(errorClass.name()+" "+errorKind.name()));
            send.terminate();
        }

        @Override
        public void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
            consumeReplyContent.accept(messageName.toString());
            send.terminate();
        }
    }

    private static Function<HttpCommunicator.HttpParamValues, KeyValue<ParamName, Message.Value>> getHttpParamValuesKeyValueFunction() {
        return httpParamValues -> RestServiceProtocol.createParamKeyValue(
                ParamName.create(httpParamValues.getParam()),
                MessageValue.values(httpParamValues.getValues().stream().map(MessageValue::text)));
    }

    public interface HttpServiceConfig extends NamedConfiguration {
        Address getRestServiceAddress();
        int getListenPort();
    }


}
