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
import org.objectagon.core.service.event.EventServiceImpl;
import org.objectagon.core.service.event.EventServiceProtocolImpl;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocolImpl;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.StorageServices;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionServiceProtocol;
import org.objectagon.core.task.ProtocolTask;
import org.objectagon.core.task.SequenceTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.OneReceiverConfigurations;

import java.util.*;

import static org.objectagon.core.utils.FindNamedConfiguration.finder;

/**
 * Created by christian on 2016-03-17.
 */

public class TestCore {


    enum InitTasks implements Task.TaskName {
        InitTestCoreTasks,
        CreateTransaction;
    }

    private static Map<String,TestCore> cores = new HashMap<>();

    public static synchronized TestCore get(String name) {
        TestCore testCore = cores.get(name);
        if (testCore==null) {
            System.out.println("TestCore.get %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% created new "+name+" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            testCore = new TestCore(name);
            Task initialized = testCore.initialize();
            try {
                TaskWait.create(initialized).startAndWait(1000L);
            } catch (UserException e) {
                throw new RuntimeException("Timout!", e);
            }
            cores.put(name, testCore);
        }
        return testCore;
    }

    Server.ServerId serverId;
    Server server;

    StorageServices storageServices;
    ObjectServices objectServices;
    Optional<TestUser> latestTestUser = Optional.empty();
    Optional<Transaction> transaction = Optional.empty();

    private TestCore(String serverId) {
        this.serverId = LocalServerId.local(serverId);
        this.server = new ServerImpl(this.serverId);

        NameServiceImpl.registerAtServer(server);
        NameServiceProtocolImpl.registerAtServer(server);
        EventServiceImpl.registerAtServer(server);
        EventServiceProtocolImpl.registerAtServer(server);
        server.createReceiver(NameServiceImpl.NAME_SERVICE, null);
        server.createReceiver(EventServiceImpl.EVENT_SERVICE_ADDRESS, null);

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
        SequenceTask sequenceTask = new SequenceTask((Receiver.ReceiverCtrl) server, InitTasks.InitTestCoreTasks);
        sequenceTask.add(storageServices.initialize());
        sequenceTask.add(objectServices.initialize());
        return sequenceTask;
    }

    private static Name TEST_USER = StandardName.name("TEST_USER");

    public Optional<TestUser> getLatestTestUser() {
        return latestTestUser;
    }

    public TestUser createTestUser(String name) {
        System.out.println("TestCore.createTestUser "+name);
        latestTestUser = Optional.of(server.createReceiver(TEST_USER, OneReceiverConfigurations.create(TEST_USER, (TestUserConfig) () -> TEST_USER)));
        return latestTestUser.get();
    }

    public void createTransaction() throws UserException {
        Message message = TaskWait.create(
                new ProtocolTask<>(
                        (Receiver.ReceiverCtrl) server,
                        InitTasks.CreateTransaction,
                        TransactionServiceProtocol.TRANSACTION_SERVICE_PROTOCOL,
                        storageServices.getTransactionServiceName(), TransactionServiceProtocol.Send::create)
        ).startAndWait(1000L);
        transaction = Optional.of(message.getValue(StandardField.ADDRESS).asAddress());
    }

    public void stop() {

    }

    public class TestUser extends AbstractReceiver<Address> {
        Name name;
        Optional<Message> message = Optional.empty();
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
            super.configure();
            TestUserConfig testUserConfig = finder(configurations).getConfigurationByName(TEST_USER);
            name = testUserConfig.getName();
        }

        @Override
        protected Address createAddress(Configurations... configurations) {
            return finder(configurations).createConfiguredAddress(StandardAddress::standard);
        }

        @Override
        public void receive(Envelope envelope) {

        }

        private Message.Values headers() {
            List<Message.Value> values = new ArrayList<>();
            transaction.map(transaction -> MessageValue.address(Transaction.TRANSACTION, transaction)).ifPresent(values::add);
            return MessageValue.values(values).asValues();
        }

        public EntityServiceProtocol.Send createFieldEntityServiceProtocol() {
            EntityServiceProtocol entityServiceProtocol = server.createReceiver(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL, null);
            return entityServiceProtocol.createSend(() -> StandardComposer.create(this, objectServices.getFieldServiceAddress(), headers()));
        }

        public EntityServiceProtocol.Send createInstanceClassEntityServiceProtocol() {
            EntityServiceProtocol entityServiceProtocol = server.createReceiver(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL, null);
            return entityServiceProtocol.createSend(() -> StandardComposer.create(this, objectServices.getInstanceClassServiceAddress(), headers()));
        }

        public InstanceClassProtocol.Send createInstanceClassProtocolSend(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
            InstanceClassProtocol instanceClassProtocol = server.createReceiver(InstanceClassProtocol.INSTANCE_CLASS_PROTOCOL, null);
            return instanceClassProtocol.createSend(() -> StandardComposer.create(this, instanceClassIdentity, headers()));
        }

        public FieldProtocol.Send createFieldProtocolSend(Field.FieldIdentity fieldIdentity) {
            FieldProtocol fieldProtocol = server.createReceiver(FieldProtocol.FIELD_PROTOCOL, null);
            return fieldProtocol.createSend(() -> StandardComposer.create(this, fieldIdentity, headers()));
        }

        public void verifyResponseMessage(ResponseMessageName responseMessageName) {
            Assert.assertEquals(message.get().getName(), responseMessageName.getMessageName());
        }
    }

    public interface TestUserConfig extends Receiver.NamedConfiguration {
        Name getName();
    }

}
