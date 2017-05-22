package org.objectagon.core.rest2.batch;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.receiver.AsyncAction;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.rest2.batch.impl.BatchUpdateImpl;
import org.objectagon.core.rest2.batch.impl.MapToBatchUpdate;
import org.objectagon.core.rest2.batch.impl.PlanImpl;
import org.objectagon.core.service.*;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionManagerProtocol;
import org.objectagon.core.storage.TransactionServiceProtocol;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;

import static org.objectagon.core.rest2.batch.BatchServiceProtocol.NAMES_FIELD;
import static org.objectagon.core.service.name.NameServiceUtils.storeLookedUpNameIn;

/**
 * Created by christian on 2017-04-14.
 */
public class BatchService extends AbstractService<BatchService.BatchServiceWorkerImpl, Service.ServiceName> {

    public static final Name BATCH_SERVICE_CONFIGURATION_NAME = StandardName.name("BATCH_SERVICE_CONFIGURATION_NAME");
    public static final ServiceName BATCH_SERVICE = StandardServiceName.name("BATCH_SERVICE");

    public static void registerAtServer(Server server) {
        server.registerFactory(BATCH_SERVICE, BatchService::new);
    }

    private enum BatchServiceTaskName implements Task.TaskName {
        BATCH_UPDATE, EXECUTE_PLAN, LookupNames,
    }

    private Name metaServiceName;
    private Name instanceClassServiceName;
    private Name transactionServiceName;

    private Address metaServiceAddress;
    private Address instanceClassServiceAddress;
    private Address transactionServiceAddress;

    @Override protected boolean logLevelCheck(WorkerContextLogKind logKind) {return true;}

    public BatchService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        setServiceName(BATCH_SERVICE);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        BatchServiceConfig batchServiceConfig = FindNamedConfiguration.finder(configurations).getConfigurationByName(BATCH_SERVICE_CONFIGURATION_NAME);
        metaServiceName = batchServiceConfig.getMetaServiceName();
        instanceClassServiceName = batchServiceConfig.getInstanceClassServiceName();
        transactionServiceName = batchServiceConfig.getTransactionServiceName();
    }

    @Override
    protected Service.ServiceName createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, addressId) ->
                StandardServiceNameAddress.name(BATCH_SERVICE, serverId, timestamp, addressId)
        );
    }

    private BatchUpdate.Targets getTargets() {
        return new BatchUpdate.Targets() {
            @Override public Composer.ResolveTarget getInstanceClassServiceAddress() {return  () -> instanceClassServiceAddress;}
            @Override public Composer.ResolveTarget getMetaServiceAddress() {return  () -> metaServiceAddress;}
        };
    }

    @Override
    protected Optional<TaskBuilder.Builder> internalCreateStartServiceTask(BatchServiceWorkerImpl serviceWorker) {
        final TaskBuilder.ParallelBuilder parallel = serviceWorker.getTaskBuilder().parallel(BatchServiceTaskName.LookupNames);
        final Optional<Address> nameServiceAddress = getReceiverCtrl().lookupAddressByAlias(NameServiceImpl.NAME_SERVICE);
        parallel.<NameServiceProtocol.Send>protocol(
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress::get,
                session -> session.lookupAddressByName(metaServiceName))
                .success(storeLookedUpNameIn(BatchService.this::setMetaServiceAddress));
        parallel.<NameServiceProtocol.Send>protocol(
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress::get,
                session -> session.lookupAddressByName(instanceClassServiceName))
                .success(storeLookedUpNameIn(BatchService.this::setInstanceClassServiceAddress));
        parallel.<NameServiceProtocol.Send>protocol(
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                nameServiceAddress::get,
                session -> session.lookupAddressByName(transactionServiceName))
                .success(storeLookedUpNameIn(BatchService.this::setTransactionServiceAddress));
        return Optional.of(parallel);
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(BatchServiceProtocol.MessageName.BATCH_UPDATE),
                (initializer, context) -> new BatchUpdateAction((BatchService) initializer, (BatchService.BatchServiceWorkerImpl) context)
        );
    }

    @Override
    protected BatchServiceWorkerImpl createWorker(WorkerContext workerContext) {
        return new BatchServiceWorkerImpl(workerContext);
    }

    public class BatchServiceWorkerImpl extends ServiceWorkerImpl {

        public BatchServiceWorkerImpl(Receiver.WorkerContext workerContext) {
            super(workerContext);
        }

        public Message.Value currentTransaction() {
            return getWorkerContext().getHeader(Transaction.TRANSACTION);
        }

        public Address getTransactionServiceAddress() {
            return transactionServiceAddress;
        }
    }

    private static class BatchUpdateAction extends AsyncAction<BatchService, BatchServiceWorkerImpl> {


        private Message.Values updateCommandField;
        private boolean newTransaction;
        private PlanImpl plan;
        private Transaction transaction;

        public BatchUpdateAction(BatchService initializer, BatchServiceWorkerImpl context) {
            super(initializer, context);
        }

        public void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
            Message.Value messageValue = plan.getActionContext()
                    .map(actionContext -> MessageValue.values(NAMES_FIELD, actionContext.asValues()))
                    .orElse(MessageValue.empty());
            context.replyWithParam(messageValue);
        }

        public boolean initialize() throws UserException {
            newTransaction = context.getValue(BatchServiceProtocol.NEW_TRANSACTION_FIELD).asText().equalsIgnoreCase("true");
            updateCommandField = context.getValue(BatchServiceProtocol.UPDATE_COMMAND_FIELD).asValues();
            if (!newTransaction && context.currentTransaction().isUnknown())
                throw new UserException(ErrorClass.BATCH_UPDATE, ErrorKind.TRANSACTION_NOT_FOUND);
            setSuccessAction(this::success);
            return true;
        }

        @Override
        protected Task internalRun(BatchServiceWorkerImpl actionContext) throws UserException {
            final TaskBuilder taskBuilder = context.getTaskBuilder();
            BatchUpdate batchUpdate = new BatchUpdateImpl(taskBuilder, initializer.getTargets());

            final TaskBuilder.SequenceBuilder sequenceBuilder = taskBuilder.sequence(BatchServiceTaskName.BATCH_UPDATE);

            if (newTransaction) {
                actionContext.trace("new Transaction");
                if (actionContext.currentTransaction().isUnknown()) {
                    sequenceBuilder.protocol(
                            TransactionServiceProtocol.TRANSACTION_SERVICE_PROTOCOL,
                            context.getTransactionServiceAddress(),
                            TransactionServiceProtocol.Send::create)
                            .firstSuccess((messageName, values) -> {
                                transaction = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
                                taskBuilder.addHeader(MessageValue.address(Transaction.TRANSACTION,transaction));
                            });
                } else {
                    sequenceBuilder.protocol(
                            TransactionManagerProtocol.TRANSACTION_MANAGER_PROTOCOL,
                            () -> actionContext.currentTransaction().asAddress(),
                            TransactionManagerProtocol.Send::extend)
                            .firstSuccess((messageName, values) -> {
                                transaction = MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress();
                                taskBuilder.addHeader(MessageValue.address(Transaction.TRANSACTION,transaction));
                            });
                }
            } else {  // No new transaction
                actionContext.trace("current Transaction");
                taskBuilder.addHeader(actionContext.currentTransaction());
            }

            sequenceBuilder.addTask(batchUpdate.createPlan(MapToBatchUpdate.transfer(updateCommandField))
                    .addSuccessAction((messageName, values) -> {
                        plan = values.iterator().next().getValue();
                    }));
            sequenceBuilder.action(BatchServiceTaskName.EXECUTE_PLAN, (success, fail) -> {
                plan.execute()
                        .addSuccessAction(success)
                        .addFailedAction(fail)
                        .start();
            });

            if (newTransaction) {
                actionContext.trace("commit Transaction");
                sequenceBuilder.protocol(
                        TransactionManagerProtocol.TRANSACTION_MANAGER_PROTOCOL,
                        () -> transaction,
                        TransactionManagerProtocol.Send::commit);
            }

            return sequenceBuilder.create();
        }
    }

    public interface BatchServiceConfig extends NamedConfiguration {
        Name getInstanceClassServiceName();
        Name getMetaServiceName();
        Name getTransactionServiceName();
    }

    void setMetaServiceAddress(Address metaServiceAddress) {this.metaServiceAddress = metaServiceAddress;}
    void setInstanceClassServiceAddress(Address instanceClassServiceAddress) {this.instanceClassServiceAddress = instanceClassServiceAddress;}
    void setTransactionServiceAddress(Address transactionServiceAddress) {this.transactionServiceAddress = transactionServiceAddress;}
}
