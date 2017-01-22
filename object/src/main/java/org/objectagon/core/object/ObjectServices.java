package org.objectagon.core.object;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.object.field.FieldProtocolImpl;
import org.objectagon.core.object.field.FieldService;
import org.objectagon.core.object.fieldvalue.FieldValueProtocolImpl;
import org.objectagon.core.object.fieldvalue.FieldValueService;
import org.objectagon.core.object.instance.InstanceProtocolImpl;
import org.objectagon.core.object.instance.InstanceService;
import org.objectagon.core.object.instanceclass.InstanceClassProtocolImpl;
import org.objectagon.core.object.instanceclass.InstanceClassService;
import org.objectagon.core.object.meta.MetaProtocolImpl;
import org.objectagon.core.object.meta.MetaService;
import org.objectagon.core.object.method.MethodProtocolImpl;
import org.objectagon.core.object.method.MethodService;
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
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.OneReceiverConfigurations;

/**
 * Created by christian on 2016-03-17.
 */
public class ObjectServices {
    public enum InitTasks implements Task.TaskName {
        InitObjectServiceTasks;
    }

    public static ObjectServices create(Server server, Service.ServiceName persistenceAddress) { return new ObjectServices(server, persistenceAddress);}

    final private Server server;
    private Address fieldServiceAddress;
    private Address fieldValueServiceAddress;
    private Address instanceServiceAddress;
    private Address instanceClassServiceAddress;
    private Address relationServiceAddress;
    private Address relationClassServiceAddress;
    private Address metaServiceAddress;
    private Address methodServiceAddress;

    Service.ServiceName persistencyServiceName;

    public ObjectServices(Server server, Service.ServiceName persistenceServiceName) {
        this.server = server;
        this.persistencyServiceName = persistenceServiceName;
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

        MetaService.registerAt(server);
        MetaProtocolImpl.registerAt(server);

        MethodService.registerAt(server);
        MethodProtocolImpl.registerAt(server);

        return this;
    }

    public ObjectServices createReceivers() {
        fieldServiceAddress = server.createReceiver(FieldService.NAME, getInitializer(Field.ENTITY_NAME)).getAddress();
        fieldValueServiceAddress = server.createReceiver(FieldValueService.NAME, getInitializer(FieldValue.ENTITY_NAME)).getAddress();
        instanceServiceAddress = server.createReceiver(InstanceService.NAME, getInitializer(Instance.ENTITY_NAME)).getAddress();
        instanceClassServiceAddress = server.createReceiver(InstanceClassService.NAME, getInitializer(InstanceClass.ENTITY_NAME)).getAddress();
        relationServiceAddress = server.createReceiver(RelationService.NAME, getInitializer(Relation.ENTITY_NAME)).getAddress();
        relationClassServiceAddress = server.createReceiver(RelationClassService.NAME, getInitializer(RelationClass.ENTITY_NAME)).getAddress();
        metaServiceAddress = server.createReceiver(MetaService.NAME, getInitializer(Meta.ENTITY_NAME)).getAddress();
        methodServiceAddress = server.createReceiver(MethodService.NAME, getInitializer(Method.ENTITY_NAME)).getAddress();
        return this;
    }

    private Receiver.Configurations getInitializer(EntityName entityName) {
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
        return OneReceiverConfigurations.create(EntityService.ENTITY_SERVICE_CONFIG_NAME, entityServiceConfig);
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

    public Address getMetaServiceAddress() {return metaServiceAddress;}

    public Address getMethodServiceAddress() {return methodServiceAddress;}

    public void initialize(TaskBuilder.SequenceBuilder sequenceBuilder) {
        Server.AliasCtrl aliasCtrl = (Server.AliasCtrl) this.server;
        Composer.ResolveTarget nameServiceAddress = () -> aliasCtrl.lookupAddressByAlias(NameServiceImpl.NAME_SERVICE).get();
        registerName(nameServiceAddress, sequenceBuilder, this.fieldServiceAddress, FieldService.NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.fieldValueServiceAddress, FieldValueService.NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.instanceServiceAddress, InstanceService.NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.instanceClassServiceAddress, InstanceClassService.NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.relationServiceAddress, RelationService.NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.relationClassServiceAddress, RelationClassService.NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.metaServiceAddress, MetaService.NAME);
        registerName(nameServiceAddress, sequenceBuilder, this.methodServiceAddress, MethodService.NAME);
    }

    private void registerName(Composer.ResolveTarget nameServiceAddress, TaskBuilder.SequenceBuilder sequenceBuilder, Address address, Service.ServiceName name) {
        sequenceBuilder.<NameServiceProtocol.Send>protocol(
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress,
                session -> session.registerName(address, name)
        );
    }

}
