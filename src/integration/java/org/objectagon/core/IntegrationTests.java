package org.objectagon.core;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.protocol.StandardProtocolImpl;
import org.objectagon.core.msg.receiver.*;
import org.objectagon.core.server.LocalServerId;
import org.objectagon.core.server.ServerImpl;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.service.name.NameServiceProtocolImpl;
import org.objectagon.core.service.name.NameServiceProtocolImplIntegration;
import org.objectagon.core.task.Task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    Receiver.CtrlId commanderCtrlId = new ReceiverCtrlIdName("integration.test.commander");
    Commander commander;

    private List<Suite> suites = new LinkedList<>();

    public IntegrationTestEntity registerTest(IntegrationTestEntity testEntity) {
        tests.add(testEntity);
        return testEntity;
    }

    public IntegrationTestEntity registerTest(String name, IntegrationTestEntityAction action, String... dependentOn) {
        return registerTest(new IntegrationTestEntityImpl(name, action, dependentOn));
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

        NameServiceProtocolImpl.registerAtServer(server);

        NameServiceImpl.registerAtServer(server);

        server.registerFactory(commanderName, new CommanderCtrl(server, commanderCtrlId));

        commander = server.createReceiver(commanderName);

        suites.stream().forEach(suite -> suite.setup(server));
    }

    void createTest() {
        suites.stream().forEach(suite -> suite.createTests(this));
    }

    @Override
    public void add(Suite suite) {
        suites.add(suite);
    }

    private class CommanderCtrl extends BasicReceiverCtrlImpl implements Server.Factory {
        public CommanderCtrl(ServerImpl server, Receiver.CtrlId ctrlId) {
            super(server, server, server, server.getServerId(), ctrlId);
        }

        @Override
        protected Address internalCreateNewAddress(Server.ServerId serverId, Receiver.CtrlId ctrlId, long addressId, Receiver.CreateNewAddressParams param) {
            return StandardAddress.standard(serverId, ctrlId, addressId);

        }

        @Override
        public <R extends Receiver> R create() {
            return (R) new Commander(this);
        }
    }

    public class Commander extends BasicReceiverImpl {
        public Commander(BasicReceiverCtrl receiverCtrl) {
            super(receiverCtrl);
        }

        @Override protected CreateNewAddressParams createNewAddressParams() {return null;}
        @Override protected BasicWorker createWorker(WorkerContext workerContext) {return new BasicWorkerImpl(workerContext);}

        @Override
        protected void handle(BasicWorker worker) {
            System.out.println("Commander.handle "+worker.getMessageName());
        }
    }

    public interface IntegrationTestEntity  {
        boolean sameName(String name);

        void resolveDependency(Tests tests);

        void addWaiter(IntegrationTestEntity waiter);

        void start();

        boolean isCompleted();

        boolean isFailed();

        void printStatus();

        boolean notCompleted();
    }

    public class IntegrationTestEntityImpl implements IntegrationTestEntity, Task.SuccessAction, Task.FailedAction {
        boolean completed = false;
        boolean success = false;
        String name;
        NameServiceProtocol.Session session;
        IntegrationTestEntityAction action;
        Task task;
        List<IntegrationTestEntityDependency> dependentOn = new LinkedList<>();
        List<IntegrationTestEntity> waiters = new LinkedList<>();

        //Reply
        Message.MessageName messageName;
        ErrorClass errorClass;
        ErrorKind errorKind;
        Iterable<Message.Value> values;

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

        public IntegrationTestEntityImpl(String name, IntegrationTestEntityAction action, String... dependentOn) {
            this.name = name;
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
                session = server.createSession(
                        NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                        StandardComposer.create(commander, NameServiceImpl.NAME_SERVICE_ADDRESS));
                task = action.action(this, commander, session);
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
            session.terminate();
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
    public interface IntegrationTestEntityAction {
        Task action(IntegrationTestEntity testEntity, Commander commander, NameServiceProtocol.Session session);
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
    }

    public static void main(String[] params) {
        IntegrationTests tests = new IntegrationTests();
        NameServiceProtocolImplIntegration.register(tests);

        tests.setup();
        tests.createTest();
        tests.setUpTestDependencies();
        tests.startTests();
    }

}
