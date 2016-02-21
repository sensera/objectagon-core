package org.objectagon.core;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.message.UnknownValue;
import org.objectagon.core.msg.protocol.StandardProtocolImpl;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.server.ServerImpl;
import org.objectagon.core.service.name.NameServiceProtocolImplIntegration;
import org.objectagon.core.storage.entity.EntityServiceProtocolImplIntegration;
import org.objectagon.core.task.Task;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.objectagon.core.utils.Util.printValuesToString;

/**
 * Created by christian on 2016-01-06.
 */
public class IntegrationTests implements Suite.RegisterSuite {

    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    Executor executor = new ThreadPoolExecutor(10, 100, 1000, TimeUnit.MILLISECONDS, queue);

    LocalServerId home = LocalServerId.local("integration.test.server");
    public ServerImpl server;

    Tests tests = new Tests();

    Name commanderName = new Name(){};
    Commander commander;

    private List<Suite> suites = new LinkedList<>();

    public IntegrationTestEntity registerTest(IntegrationTestEntity testEntity) {
        tests.add(testEntity);
        return testEntity;
    }

    public <S extends Protocol> IntegrationTestEntity registerTest(String name, Protocol.ProtocolName protocolName, Address target, IntegrationTestEntityAction<S> action, String... dependentOn) {
        return registerTest(new IntegrationTestEntityImpl<S>(name, protocolName, target, action, dependentOn));
    }

    void setUpTestDependencies() {
        tests.setUpDependencies();
    }

    void startTests() {
        tests.start();
    }

    void setup() {
        server = new ServerImpl(home);

        StandardProtocolImpl.registerAtServer(server);

        server.registerFactory(commanderName, Commander::new);

        commander = server.createReceiver(commanderName, null);

        suites.stream().forEach(suite -> suite.setup(new Suite.Setup() {
            @Override public void registerAtServer(Suite.RegisterAtServer registerAtServer) {
                registerAtServer.register(server);
            }
        }));
    }

    void createTest() {
        suites.stream().forEach(suite -> suite.createTests(this));
    }

    @Override
    public void add(Suite suite) {
        suites.add(suite);
    }


    public class Commander extends BasicReceiverImpl {
        public Commander(ReceiverCtrl receiverCtrl) {
            super(receiverCtrl);
        }

        @Override protected BasicWorker createWorker(WorkerContext workerContext) {return new BasicWorkerImpl(workerContext);}

        @Override
        protected void handle(BasicWorker worker) {
            System.out.println("Commander.handle "+worker.getMessageName());
        }

        @Override
        protected Address createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
            return StandardAddress.standard(serverId, timestamp, id);
        }
    }

    public interface IntegrationTestEntity<S extends Protocol>  {
        boolean sameName(String name);

        void resolveDependency(Tests tests);

        void addWaiter(IntegrationTestEntity waiter);

        void start();

        boolean isCompleted();

        boolean isFailed();

        void printStatus();

        boolean notCompleted();

        Message.Value getValue(Message.Field field);

        Message.Value getDependencyValue(String dependencyName, Message.Field field);
    }

    public class IntegrationTestEntityImpl<S extends Protocol> implements IntegrationTestEntity<S>, Task.SuccessAction, Task.FailedAction {
        boolean completed = false;
        boolean success = false;
        Protocol.ProtocolName protocolName;
        Address target;
        String name;
        S session;
        IntegrationTestEntityAction<S> action;
        Task task;
        List<IntegrationTestEntityDependency> dependentOn = new LinkedList<>();
        List<IntegrationTestEntity> waiters = new LinkedList<>();

        //Reply
        Message.MessageName messageName;
        ErrorClass errorClass;
        ErrorKind errorKind;
        Iterable<Message.Value> values;

        @Override
        public Message.Value getValue(Message.Field field) {
            for (Message.Value value : values)
                if (value.getField().equals(field))
                    return value;
            return UnknownValue.create(field);
        }

        @Override
        public Message.Value getDependencyValue(String dependencyName, Message.Field field) {
            return dependentOn.stream().filter(integrationTestEntityDependency -> integrationTestEntityDependency.sameName(dependencyName)).findFirst().get().getValue(field);
        }

        public boolean isCompleted() {return completed;}

        @Override
        public boolean isFailed() {
            return !success;
        }

        @Override
        public void printStatus() {
            System.out.println("::"+name+":: "+messageName+" "+errorClass+" "+errorKind+ printValuesToString(values));
        }

        @Override
        public boolean notCompleted() {
            return !completed;
        }

        public boolean sameName(String name) { return this.name.equals(name); }

        @Override
        public void resolveDependency(Tests tests) {
            dependentOn.stream().forEach(
                    dependency -> dependency.resolveDependency(tests)
            );
        }

        @Override
        public void addWaiter(IntegrationTestEntity waiter) {
            waiters.add(waiter);
        }

        public IntegrationTestEntityImpl(String name, Protocol.ProtocolName protocolName, Address target, IntegrationTestEntityAction action, String... dependentOn) {
            this.name = name;
            this.protocolName = protocolName;
            this.target = target;
            this.action = action;
            Arrays.asList(dependentOn).stream().forEach(s -> this.dependentOn.add(createDependency(s)));
        }

        IntegrationTestEntityDependency createDependency(String name) {
            return new IntegrationTestEntityDependency(this, name);
        }

        public void start() {
            synchronized (this) {
                if (!allDependenciesAreCompleted()) {
                    return;
                }
            }
            executor.execute(() -> {
                session = server.createReceiver(protocolName, null);
                Composer composer = new StandardComposer(session, target);
                task = action.action(this, commander, composer, session);
                task.addSuccessAction(this);
                task.addFailedAction(this);
                task.start();
            });
        }

        private void completed() {
            synchronized (this) {
                if (completed)
                    return;
                completed = true;
            }
            waiters.stream().forEach(
                    IntegrationTestEntity::start
            );
            tests.completed(this);
        }

        @Override
        public void failed(ErrorClass errorClass, ErrorKind errorKind, Iterable<Message.Value> values) {
            this.errorClass = errorClass;
            this.errorKind = errorKind;
            this.values = values;

            completed();
        }

        @Override
        public void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
            this.messageName = messageName;
            this.values = values;
            success = true;
            completed();
        }

        private boolean allDependenciesAreCompleted() {
            if (dependentOn.isEmpty())
                return true;
            return !dependentOn.stream().anyMatch(testEntity -> !testEntity.isCompleted());
        }

    }

    @FunctionalInterface
    public interface IntegrationTestEntityAction<P extends Protocol> {
        Task action(IntegrationTestEntity testEntity, Commander commander, Composer composer, P protocol);
    }

    public static class Tests {
        List<IntegrationTestEntity> tests = new LinkedList<>();

        public void add(IntegrationTestEntity testEntity) {
            tests.add(testEntity);
        }

        public void setUpDependencies() {
            tests.stream().forEach(testEntity -> testEntity.resolveDependency(this));
        }

        public IntegrationTestEntity lookupTestByName(String name) {
            return tests.stream()
                    .filter(testEntity -> testEntity.sameName(name))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Internal error"));
        }

        public void start() {
            tests.stream().forEach(IntegrationTestEntity::start);
        }

        public void completed(IntegrationTestEntityImpl integrationTestEntity) {
            if (allTestsAreCompleted()) {
                printTestResults();
                System.exit(anyFailedTest() ? -1 : 0);
            }
        }

        private void printTestResults() {
            System.out.println("*************************** Test results **************************************************");
            if (anyFailedTest()) {
                System.out.println("!!!!!! There are FAILED TESTS !!!!!!");
                allFailed().forEach(IntegrationTestEntity::printStatus);
            }
            int totalTestsCount = tests.size();
            int countFailed = countFailedTests();
            int countSuccess = totalTestsCount - countFailed;
            System.out.println("Total tests: "+ totalTestsCount);
            System.out.println("Failed   :"+countFailed);
            System.out.println("Succcess :"+countSuccess);

        }

        private Stream<IntegrationTestEntity> allFailed() {
            return tests.stream().filter(IntegrationTestEntity::isFailed);
        }

        private int countFailedTests() {
            return (int) tests.stream().filter(IntegrationTestEntity::isFailed).count();
        }

        private boolean anyFailedTest() {
            return tests.stream().anyMatch(IntegrationTestEntity::isFailed);
        }

        private boolean allTestsAreCompleted() {
            return !tests.stream().anyMatch(IntegrationTestEntity::notCompleted);
        }


    }

    public static class IntegrationTestEntityDependency {
        String name;
        IntegrationTestEntity owner;
        IntegrationTestEntity isDependentOf;

        public IntegrationTestEntityDependency(IntegrationTestEntity owner, String name) {
            this.owner = owner;
            this.name = name;
        }

        void resolveDependency(Tests tests) {
            isDependentOf = tests.lookupTestByName(name);
            isDependentOf.addWaiter(owner);
        }

        public boolean isCompleted() {
            return isDependentOf.isCompleted();
        }

        public boolean sameName(String dependencyName) {
            return name.equals(dependencyName);
        }

        public Message.Value getValue(Message.Field field) {
            return isDependentOf.getValue(field);
        }
    }

    public static void main(String[] params) {
        IntegrationTests tests = new IntegrationTests();
        //NameServiceProtocolImplIntegration.register(tests);
        EntityServiceProtocolImplIntegration.register(tests);

        tests.setup();
        tests.createTest();
        tests.setUpTestDependencies();
        tests.startTests();
    }

}
