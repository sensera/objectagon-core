package org.objectagon.core.object;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.object.field.FieldProtocolImpl;
import org.objectagon.core.object.field.FieldService;
import org.objectagon.core.object.fieldvalue.FieldValueProtocolImpl;
import org.objectagon.core.object.fieldvalue.FieldValueService;
import org.objectagon.core.object.instance.InstanceProtocolImpl;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.object.instanceclass.InstanceClassProtocolImpl;
import org.objectagon.core.object.instanceclass.InstanceClassService;
import org.objectagon.core.object.relation.RelationProtocolImpl;
import org.objectagon.core.object.relation.RelationService;
import org.objectagon.core.object.relationclass.RelationClassProtocolImpl;
import org.objectagon.core.object.relationclass.RelationClassService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.entity.EntityServiceProtocolImpl;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.SequenceTask;
import org.objectagon.core.task.Task;

import java.util.Optional;

/**
 * Created by christian on 2016-03-17.
 */
public class ObjectServices {
    enum InitTasks implements Task.TaskName {
        InitObjectServiceTasks;
    }

    public static ObjectServices create(Server server, Service.ServiceName persistancyAddress) { return new ObjectServices(server, persistancyAddress);}

    final private Server server;
    private Address fieldServiceAddress;
    private Address fieldValueServiceAddress;
    private Address instanceServiceAddress;
    private Address instanceClassServiceAddress;
    private Address relationServiceAddress;
    private Address relationClassServiceAddress;

    Service.ServiceName persistencyServiceName;

    public ObjectServices(Server server, Service.ServiceName persistencyServiceName) {
        this.server = server;
        this.persistencyServiceName = persistencyServiceName;
    }

    public ObjectServices registerAt() {
        EntityServiceProtocolImpl.registerAtServer(server);

        FieldService.registerAt(server);
        FieldProtocolImpl.registerAt(server);

        FieldValueService.registerAt(server);
        FieldValueProtocolImpl.registerAt(server);

        InstanceService.registerAt(server);
        InstanceProtocolImpl.registerAtServer(server);

        InstanceClassService.registerAt(server);
        InstanceClassProtocolImpl.registerAt(server);

        RelationService.registerAt(server);
        RelationProtocolImpl.registerAt(server);

        RelationClassService.registerAt(server);
        RelationClassProtocolImpl.registerAt(server);

        return this;
    }

    public ObjectServices createReceivers() {
        fieldServiceAddress = server.createReceiver(FieldService.NAME, getInitializer(Field.ENTITY_NAME)).getAddress();
        fieldValueServiceAddress = server.createReceiver(FieldValueService.NAME, getInitializer(FieldValue.ENTITY_NAME)).getAddress();
        instanceServiceAddress = server.createReceiver(InstanceService.NAME, getInitializer(Instance.ENTITY_NAME)).getAddress();
        instanceClassServiceAddress = server.createReceiver(InstanceClassService.NAME, getInitializer(InstanceClass.ENTITY_NAME)).getAddress();
        relationServiceAddress = server.createReceiver(RelationService.NAME, getInitializer(Relation.ENTITY_NAME)).getAddress();
        relationClassServiceAddress = server.createReceiver(RelationClassService.NAME, getInitializer(RelationClass.ENTITY_NAME)).getAddress();
        return this;
    }

    private Receiver.Initializer<Service.ServiceName> getInitializer(EntityName entityName) {
        EntityService.EntityServiceConfig entityServiceConfig = new EntityService.EntityServiceConfig() {
            @Override
            public EntityName getEntityName() {
                return entityName;
            }

            @Override
            public Service.ServiceName getPersistencyService() {
                return persistencyServiceName;
            }
        };
        return new Receiver.Initializer<Service.ServiceName>() {
            @Override public <C extends Receiver.SetInitialValues> C initialize(Service.ServiceName address) {return (C) entityServiceConfig;}
        };
    }

    public Server getServer() {
        return server;
    }

    public Address getFieldServiceAddress() {
        return fieldServiceAddress;
    }

    public Address getFieldValueServiceAddress() {
        return fieldValueServiceAddress;
    }

    public Address getInstanceServiceAddress() {
        return instanceServiceAddress;
    }

    public Address getInstanceClassServiceAddress() {
        return instanceClassServiceAddress;
    }

    public Address getRelationServiceAddress() {
        return relationServiceAddress;
    }

    public Address getRelationClassServiceAddress() {
        return relationClassServiceAddress;
    }

    public Task initialize() {
        Optional<Address> nameServiceAddress = ((Server.AliasCtrl)server).lookupAddressByAlias(NameServiceImpl.NAME_SERVICE);
        SequenceTask sequenceTask = new SequenceTask((Receiver.ReceiverCtrl) server, InitTasks.InitObjectServiceTasks);
        registerName(nameServiceAddress, sequenceTask, this.fieldServiceAddress, FieldService.NAME);
        registerName(nameServiceAddress, sequenceTask, this.fieldValueServiceAddress, FieldValueService.NAME);
        registerName(nameServiceAddress, sequenceTask, this.instanceServiceAddress, InstanceService.NAME);
        registerName(nameServiceAddress, sequenceTask, this.instanceClassServiceAddress, InstanceClassService.NAME);
        registerName(nameServiceAddress, sequenceTask, this.relationServiceAddress, RelationService.NAME);
        registerName(nameServiceAddress, sequenceTask, this.relationClassServiceAddress, RelationClassService.NAME);
        return sequenceTask;
    }

    private void registerName(Optional<Address> nameServiceAddress, SequenceTask sequenceTask, Address address, Service.ServiceName name) {
        ProtocolTask<NameServiceProtocol.Send> sendProtocolTask = new ProtocolTask<>(
                (Receiver.ReceiverCtrl) server,
                InitTasks.InitObjectServiceTasks,
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress.get(), session -> {
            return session.registerName(address, name);
        });
        sequenceTask.add(sendProtocolTask);
    }

}
