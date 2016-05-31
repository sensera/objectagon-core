package feature.util;

import org.junit.Assert;
import org.objectagon.core.Server;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocolImpl;
import org.objectagon.core.msg.receiver.AbstractReceiver;
import org.objectagon.core.object.*;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.server.ServerImpl;
import org.objectagon.core.service.ServiceProtocol;
import org.objectagon.core.service.ServiceProtocolImpl;
import org.objectagon.core.service.event.BroadcastEventServiceProtocolImpl;
import org.objectagon.core.service.event.EventServiceImpl;
import org.objectagon.core.service.event.EventServiceProtocolImpl;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocolImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.task.StandardTaskBuilder;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.OneReceiverConfigurations;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.objectagon.core.utils.FindNamedConfiguration.finder;

/**
 * Created by christian on 2016-03-17.
 */

public class TestCore {

    public static long timeout = 100000000L;

    enum InitTasks implements Task.TaskName {
        InitTestCoreTasks,
        CreateTransaction,
        StartNameService,
        StartEventService,
    }

    private static Map<String,TestCore> cores = new HashMap<>();

    public static synchronized TestCore get(String name) {
        TestCore testCore = cores.get(name);
        if (testCore==null) {
            System.out.println("TestCore.get %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% created new "+name+" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            testCore = new TestCore(name);
            Task initialized = testCore.initialize();
            try {
                TaskWait.create(initialized).startAndWait(timeout*2);
            } catch (UserException e) {
                throw new RuntimeException("Timout!", e);
            }
            cores.put(name, testCore);
        }
        return testCore;
    }

    Server.ServerId serverId;
    Server server;

    Address nameService;
    Address eventService;

    StorageServices storageServices;
    ObjectServices objectServices;
    Optional<TestUser> latestTestUser = Optional.empty();
    Optional<Transaction> transaction = Optional.empty();

    private TestCore(String serverId) {
        this.serverId = LocalServerId.local(serverId);
        this.server = new ServerImpl(this.serverId);

        StandardTaskBuilder.registerAt(server);

        ServiceProtocolImpl.registerAtServer(server);
        NameServiceImpl.registerAtServer(server);
        NameServiceProtocolImpl.registerAtServer(server);
        EventServiceImpl.registerAtServer(server);
        EventServiceProtocolImpl.registerAtServer(server);
        BroadcastEventServiceProtocolImpl.registerAtServer(server);
        nameService = server.createReceiver(NameServiceImpl.NAME_SERVICE).getAddress();
        eventService = server.createReceiver(EventServiceImpl.EVENT_SERVICE_NAME).getAddress();

        StandardProtocolImpl.registerAtServer(server);

        storageServices = StorageServices.create(server)
                .registerAt()
                .createReceivers();

        objectServices = ObjectServices.create(server, storageServices.getPersistenceServiceName())
                .registerAt()
                .createReceivers();

        server.registerFactory(TEST_USER, TestUser::new);
    }

    private Task initialize() {
        TaskBuilder taskBuilder = server.createReceiver(StandardTaskBuilder.STANDARD_TASK_BUILDER);

        TaskBuilder.SequenceBuilder sequence = taskBuilder.sequence(InitTasks.InitTestCoreTasks);

        sequence.protocol(
                ServiceProtocol.SERVICE_PROTOCOL,
                nameService,
                ServiceProtocol.Send::startService
        );
        sequence.protocol(
                ServiceProtocol.SERVICE_PROTOCOL,
                eventService,
                ServiceProtocol.Send::startService
        );
        storageServices.initialize(sequence);
        objectServices.initialize(sequence);
        return sequence.create();
    }

    private Map<String, Address> nameAddress = new HashMap<>();

    public void storeNamedAddress(String name, Address address) {
        nameAddress.put(name, address);
    }

    public <A extends Address> A getNamedAddress(String name) {
        Address address = nameAddress.get(name);
        if (address == null)
            throw new RuntimeException("Name("+name+") not found!");
        return (A) address;
    }


    private static Name TEST_USER = StandardName.name("TEST_USER");

    public Optional<TestUser> getLatestTestUser() {
        return latestTestUser;
    }

    public Transaction getActiveTransaction() {
        return transaction.get();
    }
    public void setActiveTransaction(Transaction transaction) {
        this.transaction = Optional.ofNullable(transaction);
    }

    public Map<String,TestUser> testUsers = new HashMap<>();

    public Optional<TestUser> getTestUser(String name) {
        return Optional.ofNullable(testUsers.get(name));
    }

    public TestUser createTestUser(String name) {
        return getTestUser(name).orElseGet( () -> {
            System.out.println("TestCore.createTestUser " + name);
            latestTestUser = Optional.of(server.createReceiver(TEST_USER, OneReceiverConfigurations.create(TEST_USER, (TestUserConfig) () -> TEST_USER)));
            testUsers.put(name, latestTestUser.get());
            return latestTestUser.get();
        });
    }

    Transaction internalCreateTransaction() throws UserException {
        TaskBuilder taskBuilder = server.createReceiver(StandardTaskBuilder.STANDARD_TASK_BUILDER);
        TaskBuilder.Builder<Task> builder = taskBuilder.protocol(TransactionServiceProtocol.TRANSACTION_SERVICE_PROTOCOL,
                storageServices.getTransactionServiceName(), TransactionServiceProtocol.Send::create);
        Message message = TaskWait.create(
                builder.create()
        ).startAndWait(timeout);
        return message.getValue(StandardField.ADDRESS).asAddress();
    }

    Transaction internalExtendTransaction(Transaction transaction) throws UserException {
        TaskBuilder taskBuilder = server.createReceiver(StandardTaskBuilder.STANDARD_TASK_BUILDER);
        TaskBuilder.Builder<Task> builder = taskBuilder.protocol(
                TransactionManagerProtocol.TRANSACTION_MANAGER_PROTOCOL,
                transaction,
                TransactionManagerProtocol.Send::extend
        );
        Message message = TaskWait.create(
                builder.create()
        ).startAndWait(timeout);
        return message.getValue(StandardField.ADDRESS).asAddress();
    }

    public void stop() {

    }

    public class TestUser extends AbstractReceiver<Address> {
        Name name;
        Optional<Message> message = Optional.empty();
        Optional<Transaction> transaction = Optional.empty();
        Map<Message.Field,Message.Value> storedValues = new HashMap<>();

        public void setValue(Message.Field field, Message.Value value) { storedValues.put(field, value); }
        public Optional<Message.Value> getValue(Message.Field field) { return Optional.ofNullable(storedValues.get(field)); }

        public void storeResponseMessage(Message message) {
            this.message = Optional.ofNullable(message);
        }

        public TestUser(ReceiverCtrl receiverCtrl) {
            super(receiverCtrl);
        }

        @Override
        public void configure(Configurations... configurations) {
            super.configure(configurations);
            TestUserConfig testUserConfig = finder(configurations).getConfigurationByName(TEST_USER);
            name = testUserConfig.getName();
        }

        @Override
        protected Address createAddress(Configurations... configurations) {
            return finder(configurations).createConfiguredAddress(StandardAddress::standard);
        }

        @Override
        public void receive(Envelope envelope) {
            envelope.unwrap((sender, msg) ->
                System.out.println("TestUser.receive "+msg+" from "+sender)
            );
        }

        private Message.Values headers() {
            List<Message.Value> values = new ArrayList<>();
            transaction.map(transaction -> MessageValue.address(Transaction.TRANSACTION, transaction)).ifPresent(values::add);
            return MessageValue.values(values).asValues();
        }

        public EntityServiceProtocol.Send createFieldEntityServiceProtocol() {
            EntityServiceProtocol entityServiceProtocol = server.createReceiver(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL);
            return entityServiceProtocol.createSend(() -> StandardComposer.create(this, objectServices.getFieldServiceAddress(), headers()));
        }

        public EntityServiceProtocol.Send createInstanceClassEntityServiceProtocol() {
            EntityServiceProtocol entityServiceProtocol = server.createReceiver(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL);
            return entityServiceProtocol.createSend(() -> StandardComposer.create(this, objectServices.getInstanceClassServiceAddress(), headers()));
        }

        public EntityServiceProtocol.Send createMetaEntityServiceProtocol() {
            EntityServiceProtocol entityServiceProtocol = server.createReceiver(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL);
            return entityServiceProtocol.createSend(() -> StandardComposer.create(this, objectServices.getMetaServiceAddress(), headers()));
        }

        public InstanceClassProtocol.Send createInstanceClassProtocolSend(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
            InstanceClassProtocol instanceClassProtocol = server.createReceiver(InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL);
            return instanceClassProtocol.createSend(() -> StandardComposer.create(this, instanceClassIdentity, headers()));
        }

        public FieldProtocol.Send createFieldProtocolSend(Field.FieldIdentity fieldIdentity) {
            FieldProtocol fieldProtocol = server.createReceiver(FieldProtocol.FIELD_PROTOCOL);
            return fieldProtocol.createSend(() -> StandardComposer.create(this, fieldIdentity, headers()));
        }

        public MetaProtocol.Send createMetaProtocolSend(Meta.MetaIdentity metaIdentity) {
            MetaProtocol metaProtocol = server.createReceiver(MetaProtocol.META_PROTOCOL);
            return metaProtocol.createSend(() -> StandardComposer.create(this, metaIdentity, headers()));
        }

        public MethodProtocol.Send createMethodProtocolSend(Method.MethodIdentity methodIdentity) {
            MethodProtocol methodProtocol = server.createReceiver(MethodProtocol.METHOD_PROTOCOL);
            return methodProtocol.createSend(() -> StandardComposer.create(this, methodIdentity, headers()));
        }

        public InstanceProtocol.Send createInstanceProtocolSend(Instance.InstanceIdentity instanceIdentity) {
            InstanceProtocol instanceProtocol = server.createReceiver(InstanceProtocol.INSTANCE_PROTOCOL);
            return instanceProtocol.createSend(() -> StandardComposer.create(this, instanceIdentity, headers()));
        }

        public void verifyResponseMessage(ResponseMessageName responseMessageName) {
            Assert.assertEquals(message.get().getName(), responseMessageName.getMessageName());
        }

        public Transaction createTransaction() throws UserException {
            Transaction transaction = internalCreateTransaction();
            this.transaction = Optional.ofNullable(transaction);
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = Optional.ofNullable(transaction);
        }

        public void commitTransaction() throws UserException {
            commitTransaction(this.transaction.get());
        }

        public void commitTransaction(Transaction transaction) throws UserException {
            TaskBuilder taskBuilder = server.createReceiver(StandardTaskBuilder.STANDARD_TASK_BUILDER);
            TaskBuilder.Builder<Task> builder = taskBuilder.protocol(TransactionManagerProtocol.TRANSACTION_MANAGER_PROTOCOL,
                    transaction, TransactionManagerProtocol.Send::commit);
            TaskWait.create(builder.create()).startAndWait(timeout);
        }

        public List<Identity> getTransactionTargets(Transaction transaction) throws UserException {
            TaskBuilder taskBuilder = server.createReceiver(StandardTaskBuilder.STANDARD_TASK_BUILDER);
            TaskBuilder.Builder<Task> builder = taskBuilder.<TransactionManagerProtocol.Send>protocol(TransactionManagerProtocol.TRANSACTION_MANAGER_PROTOCOL,
                    transaction, session -> session.getTargets());

            Message message = TaskWait.create(builder.create()).startAndWait(timeout);
            Message.Values values = message.getValue(StandardField.VALUES).asValues();
            return StreamSupport.stream(values.values().spliterator(), false).map(value -> (Identity) value.asAddress()).collect(Collectors.toList());
        }


        public Transaction getActiveTransaction() {
            return transaction.get();
        }

        public Transaction extendTransaction(Transaction transaction) throws UserException {
            return internalExtendTransaction(transaction);
        }
    }

    public interface TestUserConfig extends Receiver.NamedConfiguration {
        Name getName();
    }

}
