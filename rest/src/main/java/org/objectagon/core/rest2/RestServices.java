package org.objectagon.core.rest2;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.rest2.http.HttpService;
import org.objectagon.core.rest2.service.RestService;
import org.objectagon.core.rest2.service.RestServiceProtocolImpl;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceProtocol;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.OneReceiverConfigurations;

/**
 * Created by christian on 2016-03-17.
 */
public class RestServices {
    public enum InitTasks implements Task.TaskName {
        InitRestTasks;
    }

    public static RestServices create(Server server) { return new RestServices(server);}

    final private Server server;
    private Address httpServiceAddress;
    private Address restServiceAddress;

    public RestServices(Server server) {
        this.server = server;
    }

    public RestServices registerAt() {
        HttpService.registerAtServer(server);

        RestServiceProtocolImpl.registerAtServer(server);
        RestService.registerAtServer(server);

        return this;
    }

    public RestServices createReceivers() {
        restServiceAddress = server.createReceiver(RestService.REST_SERVICE_NAME).getAddress();
        httpServiceAddress = server.createReceiver(HttpService.HTTP_SERVICE_NAME, OneReceiverConfigurations.create(HttpService.HTTP_SERVICE_CONFIGURATION_NAME, getHttpServiceConfig())).getAddress();
        return this;
    }

    private Receiver.NamedConfiguration getHttpServiceConfig() {
        return new HttpService.HttpServiceConfig() {
            @Override public Address getRestServiceAddress() {return restServiceAddress;}
            @Override public int getListenPort() {return 9900;}
        };
    }

    public Server getServer() {
        return server;
    }

    public Address getHttpServiceAddress() {return httpServiceAddress;}
    public Address getRestServiceAddress() {return restServiceAddress;}

    public void initialize(TaskBuilder.SequenceBuilder sequenceBuilder) {
        Server.AliasCtrl aliasCtrl = (Server.AliasCtrl) this.server;
        Composer.ResolveTarget nameServiceAddress = () -> aliasCtrl.lookupAddressByAlias(NameServiceImpl.NAME_SERVICE).get();
        registerName(nameServiceAddress, sequenceBuilder, this.httpServiceAddress, HttpService.HTTP_SERVICE_NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.restServiceAddress, RestService.REST_SERVICE_NAME);

        sequenceBuilder.protocol(
                ServiceProtocol.SERVICE_PROTOCOL,
                restServiceAddress,
                ServiceProtocol.Send::startService
        );

        sequenceBuilder.protocol(
                ServiceProtocol.SERVICE_PROTOCOL,
                httpServiceAddress,
                ServiceProtocol.Send::startService
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
