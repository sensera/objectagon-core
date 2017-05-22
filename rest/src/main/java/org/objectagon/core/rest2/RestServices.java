package org.objectagon.core.rest2;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.object.instanceclass.InstanceClassService;
import org.objectagon.core.object.meta.MetaService;
import org.objectagon.core.rest2.batch.BatchService;
import org.objectagon.core.rest2.batch.BatchServiceProtocolImpl;
import org.objectagon.core.rest2.http.HttpServerImpl;
import org.objectagon.core.rest2.http.HttpService;
import org.objectagon.core.rest2.service.RestService;
import org.objectagon.core.rest2.service.RestServiceProtocolImpl;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceProtocol;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.transaction.TransactionService;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.AddressName;
import org.objectagon.core.utils.OneReceiverConfigurations;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-03-17.
 */
public class RestServices {

    private int port;

    public enum InitTasks implements Task.TaskName {
        InitRestTasks;
    }

    public static RestServices create(Server server, int port) { return new RestServices(server, port);}

    final private Server server;
    private Address httpServiceAddress;
    private Address restServiceAddress;
    private Address batchServiceAddress;

    public RestServices(Server server, int port) {
        this.server = server;
        this.port = port;
    }

    public RestServices registerAt() {
        HttpService.registerAtServer(server);

        RestServiceProtocolImpl.registerAtServer(server);
        RestService.registerAtServer(server);

        BatchServiceProtocolImpl.registerAtServer(server);
        BatchService.registerAtServer(server);

        return this;
    }

    public RestServices createReceivers() {
        restServiceAddress = server.createReceiver(RestService.REST_SERVICE,
                OneReceiverConfigurations.create(RestService.REST_SERVICE_CONFIGURATION_NAME, getRestServiceConfig())
        ).getAddress();
        httpServiceAddress = server.createReceiver(HttpService.HTTP_SERVICE,
                OneReceiverConfigurations.create(HttpService.HTTP_SERVICE_CONFIGURATION_NAME, getHttpServiceConfig())
        ).getAddress();
        batchServiceAddress  = server.createReceiver(BatchService.BATCH_SERVICE,
                OneReceiverConfigurations.create(BatchService.BATCH_SERVICE_CONFIGURATION_NAME, getBatchServiceConfig())
                ).getAddress();
        return this;
    }

    private BatchService.BatchServiceConfig getBatchServiceConfig() {
        return new BatchService.BatchServiceConfig() {
            @Override public Name getInstanceClassServiceName() {return InstanceClassService.NAME;}
            @Override public Name getMetaServiceName() {return MetaService.NAME;}
            @Override public Name getTransactionServiceName() {return TransactionService.NAME;}
        };
    }

    private Receiver.NamedConfiguration getRestServiceConfig() {
        return (RestService.RestServiceConfig) RestServiceActionLocatorImpl::new;
    }

    private Receiver.NamedConfiguration getHttpServiceConfig() {
        return new HttpService.HttpServiceConfig() {
            @Override public Address getRestServiceAddress() {return restServiceAddress;}
            @Override public int getListenPort() {return port;}
            @Override public HttpService.CreateHttpServer getCreateHttpServer() {return HttpServerImpl::new;}
        };
    }

    public Server getServer() {
        return server;
    }

    public Address getHttpServiceAddress() {return httpServiceAddress;}
    public Address getRestServiceAddress() {return restServiceAddress;}
    public Address getBatchServiceAddress() {return batchServiceAddress;}

    public void initialize(TaskBuilder.SequenceBuilder sequenceBuilder) {
        Server.AliasCtrl aliasCtrl = (Server.AliasCtrl) this.server;
        Composer.ResolveTarget nameServiceAddress = () -> aliasCtrl.lookupAddressByAlias(NameServiceImpl.NAME_SERVICE).get();

        getServices()
                .peek(addressServiceNameAddressName -> registerName(nameServiceAddress,
                        sequenceBuilder,
                        addressServiceNameAddressName.getKey(),
                        addressServiceNameAddressName.getValue()))
                .forEach(addressServiceNameAddressName -> sequenceBuilder.protocol(
                        ServiceProtocol.SERVICE_PROTOCOL,
                        addressServiceNameAddressName.getKey(),
                        ServiceProtocol.Send::startService
                ));

    }

    private Stream<AddressName<Address, Service.ServiceName>> getServices() {
        return Stream.of(
            AddressName.create(this.httpServiceAddress, HttpService.HTTP_SERVICE),
            AddressName.create(this.restServiceAddress, RestService.REST_SERVICE),
            AddressName.create(this.batchServiceAddress, BatchService.BATCH_SERVICE)
        );
    }

    private void registerName(Composer.ResolveTarget nameServiceAddress, TaskBuilder.SequenceBuilder sequenceBuilder, Address address, Service.ServiceName name) {
        sequenceBuilder.<NameServiceProtocol.Send>protocol(
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress,
                session -> session.registerName(address, name)
        );
    }



}
