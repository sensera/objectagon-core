package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.entity.util.DataVersionTransactions;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityImpl<I extends Identity, D extends Data<I,V>, V extends Version, W extends EntityWorker>  extends BasicReceiverImpl<I, W> implements Entity<I, D> {

    private long dataVersionCounter = 0;
    private DataVersion<I,V> dataVersion;
    private Map<V,D> dataCache = new HashMap<>();
    private Data.Type dataType;

    public DataVersion<I, V> getDataVersion() {
        return dataVersion;
    }

    public EntityImpl(ReceiverCtrl receiverCtrl, Data.Type dataType) {
        super(receiverCtrl);
        this.dataType = dataType;
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        EntityConfig entityConfig = FindNamedConfiguration.finder(configurations).getConfigurationByName(Entity.ENTITY_CONFIG_NAME);
        dataVersion = entityConfig.getDataVersion(getAddress());
        dataVersionCounter = entityConfig.getDataVersionCounter();
    }

    protected abstract V internalCreateNewVersionForDataVersion(long counterNumber);

    protected abstract D createNewData();

    protected Data.Type getDataType() { return dataType; }

    protected V nextDataVersionVersion() { return internalCreateNewVersionForDataVersion(dataVersionCounter++);}

    protected Optional<D> getCachedDataByVersion(V version) {
        return Optional.ofNullable(dataCache.get(version));
    }

    protected Optional<D> getCachedDataByTransaction(Transaction transaction) {
        return new NodeFinder(transaction)
                .startFinder(dataVersion.rootNode())
                .map(vNode -> dataCache.get(vNode.getVersion()));
    }

    protected Optional<V> getGetVersionFromTransaction(Transaction transaction) {
        return new NodeFinder(transaction)
                .startFinder(dataVersion.rootNode())
                .map(DataVersion.TransactionVersionNode::getVersion);
    }

    private void updateDataAndVersion(D newData, DataVersion<I, V> newDataVersion) {
        System.out.println("EntityImpl.updateDataAndVersion "+newData);
        System.out.println("EntityImpl.updateDataAndVersion "+dataVersion);
        this.dataVersion = newDataVersion;
        this.dataCache.put(newData.getVersion(), newData);
    }

    private class NodeFinder {
        Optional<DataVersion.TransactionVersionNode<V>> res = Optional.empty();
        Transaction transaction;

        public NodeFinder(Transaction transaction) {
            this.transaction = transaction;
        }

        private Optional<DataVersion.TransactionVersionNode<V>> startFinder(Optional<DataVersion.TransactionVersionNode<V>> transactionVersionNodeOption) {
            return transactionVersionNodeOption.isPresent()
                    ? transactionVersionNodeOption.filter(vTransactionVersionNode -> vTransactionVersionNode!=null).map(this::nodeFinder)
                    : Optional.empty();
        }

        private DataVersion.TransactionVersionNode<V> nodeFinder(DataVersion.TransactionVersionNode<V> transactionVersionNode) {
            if (Objects.equals(transactionVersionNode.getTransaction(), transaction)) {
                res = Optional.of(transactionVersionNode);
            } else {
                transactionVersionNode.getNextVersion().ifPresent(this::nodeFinder);
                //transactionVersionNode.getBranchVersion().ifPresent(this::nodeFinder);
            }
            return res.get();
        }
    }

    abstract protected D upgrade(D data, DataVersion<I, V> newDataVersion, Transaction transaction);

    private void commit(EntityWorker entityWorker) throws UserException {
        Transaction transaction = entityWorker.currentTransaction();
        V version = getGetVersionFromTransaction(transaction).orElseThrow(() -> new SevereError(ErrorClass.ENTITY, ErrorKind.INCONSISTENCY, MessageValue.address(transaction)));

        DataVersion<I,V> dataVersion = getDataVersion();
        DataVersion<I,V> newDataVersion = dataVersion.<DataVersion.ChangeDataVersion<I,V>>change().commit(transaction).create(null);

        DataVersion.TransactionVersionNode<V> vTransactionVersionNode = new NodeFinder(transaction).startFinder(newDataVersion.rootNode()).get();

        switch(vTransactionVersionNode.getMergeStrategy()) {
            case Uppgrade: {
                D oldData = getCachedDataByVersion(version).get(); //TODO Load when not cached
                D newData = upgrade(oldData, newDataVersion, transaction);
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newData, newDataVersion)
                        .addFailedAction(entityWorker::failed)
                        .addSuccessAction(entityWorker::success)
                        .start();
                break;
            }
            case OverWrite: {
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newDataVersion)
                        .addFailedAction(entityWorker::failed)
                        .addSuccessAction(entityWorker::success)
                        .start();
            }
            default:
                throw new SevereError(ErrorClass.ENTITY, ErrorKind.NOT_IMPLEMENTED);
        }
    }

    private void rollback(EntityWorker entityWorker) throws UserException{
        Transaction transaction = entityWorker.currentTransaction();
        V version = getGetVersionFromTransaction(transaction).orElseThrow(() -> new SevereError(ErrorClass.ENTITY, ErrorKind.INCONSISTENCY, MessageValue.address(transaction)));

        DataVersion<I,V> dataVersion = getDataVersion();
        DataVersion<I,V> newDataVersion = null;
        newDataVersion = dataVersion.<DataVersion.ChangeDataVersion<I,V>>change().rollback(transaction).create(null);

        DataVersion.TransactionVersionNode<V> vTransactionVersionNode = new NodeFinder(transaction).startFinder(newDataVersion.rootNode()).get();

        switch(vTransactionVersionNode.getMergeStrategy()) {
            case Uppgrade: {
                D oldData = getCachedDataByVersion(version).get(); //TODO Load when not cached
                D newData = upgrade(oldData, newDataVersion, transaction);
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newData, newDataVersion)
                        .addFailedAction(entityWorker::failed)
                        .addSuccessAction(entityWorker::success)
                        .start();
                break;
            }
            case OverWrite: {
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newDataVersion)
                        .addFailedAction(entityWorker::failed)
                        .addSuccessAction(entityWorker::success)
                        .start();
            }
            default:
                throw new SevereError(ErrorClass.ENTITY, ErrorKind.NOT_IMPLEMENTED);
        }
    }

    @Override
    protected void handle(W worker) {
        triggerBuilder(worker)
                .trigger(EntityProtocol.MessageName.ADD_TO_TRANSACTION, this::addToTransaction)
                .trigger(EntityProtocol.MessageName.REMOVE_FROM_TRANSACTION, this::removeFromTransaction)
                .trigger(EntityProtocol.MessageName.DELETE, this::delete)
                .trigger(EntityProtocol.MessageName.COMMIT, this::commit)
                .trigger(EntityProtocol.MessageName.ROLLBACK, this::rollback)
                .trigger(EntityProtocol.MessageName.LOCK, this::lock)
                .trigger(EntityProtocol.MessageName.UNLOCK, this::unlock)
                .orElseThrowUnhandled();
    }

    private void addToTransaction(W w) {
        Transaction transaction = w.getValue(StandardField.ADDRESS).asAddress();

        if (getGetVersionFromTransaction(transaction).isPresent()) {
            w.replyWithError(ErrorKind.TRANSACTION_ALREADY_PRESENT);
            return;
        }

        try {
            w.start(
                w.createPersistenceServiceProtocolSend(PersistenceService.NAME).pushData(dataVersion.<DataVersion.ChangeDataVersion<I, V>>change()
                        .newVersion(transaction, v1 -> {})
                        .create(nextDataVersionVersion()))
            );
        } catch (UserException e) {
            w.replyWithError(e);
        }
    }

    private void removeFromTransaction(W w) {
        Transaction transaction = w.getValue(StandardField.ADDRESS).asAddress();

        if (!getGetVersionFromTransaction(transaction).isPresent()) {
            w.replyWithError(ErrorKind.TRANSACTION_ALREADY_PRESENT);
            return;
        }

        getCachedDataByTransaction(transaction).ifPresent(data -> dataCache.remove(data.getVersion()));
        try {
            w.start(
                    w.createPersistenceServiceProtocolSend(PersistenceService.NAME).pushData(dataVersion.<DataVersion.ChangeDataVersion<I, V>>change()
                            .remove(transaction)
                            .create(nextDataVersionVersion()))
            );
        } catch (UserException e) {
            w.replyWithError(e);
        }
    }

    private void delete(W w) {
        //TODO implement me
        w.replyOk();
    }

    private void lock(W w) {
        //TODO implement me
        w.replyOk();
    }

    private void unlock(W w) {
        //TODO implement me
        w.replyOk();
    }


    public RunWorker<W> read(ReadActionConsumer<W,D> consumer) {
        return worker -> new ReadDataAction(worker, consumer).execute();
    }

    public <C extends Data.Change<I,V>> RunWorker<W> write(WriteActionConsumer<W,D,C> writeConsumer) {
        return worker -> {
            WriteDataAction<C> writeDataAction = new WriteDataAction<C>(writeConsumer);
            new ReadDataAction(worker, writeDataAction).execute();
        };
    }

    public <C extends Data.Change<I,V>> RunWorker<W> write(WriteActionConsumer<W,D,C> writeConsumer, DataLoadedConsumer<W,D> onDataRead) {
        return worker -> {
            new ReadDataAction(worker, (worker1, data) -> {
                Optional<Task> optionalPrepareTask = onDataRead.prepare(worker, data);
                if (optionalPrepareTask.isPresent()) {
                    optionalPrepareTask.get()
                            .addFailedAction(worker::failed)
                            .addSuccessAction((messageName, values) -> {
                                new WriteDataAction<C>(writeConsumer, MessageValue.values(values).asValues()).doDataRead(worker, data);
                            })
                            .start();
                } else
                    new WriteDataAction<C>(writeConsumer).doDataRead(worker, data);
            }).execute();
        };
    }

    public class ReadDataAction {
        W worker;
        private ReadActionConsumer<W,D> consumer;

        public ReadDataAction(W worker, ReadActionConsumer<W,D> consumer) {
            this.worker = worker;
            this.consumer = consumer;
        }

        public void execute() {
            Transaction transaction = worker.currentTransaction();
            Optional<D> cachedDataByTransaction = getCachedDataByTransaction(transaction);
            if (cachedDataByTransaction.isPresent()) {
                try {
                    doDataRead(cachedDataByTransaction.get());
                } catch (UserException e) {
                    worker.replyWithError(e.getErrorKind());
                }
                return;
            }
            final Optional<V> verisionOption = getGetVersionFromTransaction(transaction);

            verisionOption.ifPresent((version)-> worker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                    .getData(getDataType(), getAddress(), version)
                    .addFailedAction(worker::failed)
                    .addSuccessAction((messageName, values) -> doDataRead(Util.getValueByField(values, Data.DATA).getValue()))
                    .start()
            );
            verisionOption.orElseGet(()-> {
                try {
                    doDataRead(createNewData());
                } catch (UserException e) {
                    worker.replyWithError(e);
                }
                return null;
            });
        }

        private void doDataRead(D data) throws UserException {
            consumer.doDataRead(worker, data);
        }
    }

    protected DataVersion<I, V> newDataVersion(Transaction transaction, Consumer<V> receiveNewVersion) throws UserException {
        DataVersion.ChangeDataVersion<I, V> change = getDataVersion().change();
        if (!DataVersionTransactions.create(getDataVersion()).transactionExists(transaction)) {
            change.add(transaction, receiveNewVersion, Data.MergeStrategy.OverWrite);
        } else
            change.newVersion(transaction, receiveNewVersion);
        return change
                .setMergeStrategy(transaction, Data.MergeStrategy.OverWrite)
                .create(nextDataVersionVersion());
    }

    protected void pushToPersistence(W worker, D newData, DataVersion<I, V> newDataVersion, Message.Values preparedValues) {
        worker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                .pushData(newData, newDataVersion)
                .addFailedAction(worker::failed)
                .addSuccessAction((messageName, values) -> {
                    updateDataAndVersion(newData, newDataVersion);
                    worker.success(StandardProtocol.MessageName.OK_MESSAGE, preparedValues!=null ? preparedValues.values():values);
                })
                .start();
    }

    protected  <C extends Data.Change<I,V>> D setData(D data, V nextVersion, Consumer<C> change) {
        C changeData = (C) data.<C>change();
        change.accept(changeData);
        return changeData.create(nextVersion);
    }

    @FunctionalInterface
    public interface ReadActionConsumer<W extends EntityWorker, D extends Data> {
        void doDataRead(W worker, D data) throws UserException;
    }

    @FunctionalInterface
    public interface WriteActionConsumer<W extends EntityWorker, D extends Data, C extends Data.Change> {
        void doDataWrite(W worker, D data, C change, Message.Values preparedValues);
    }

    @FunctionalInterface
    public interface DataLoadedConsumer<W extends EntityWorker, D extends Data> {
        Optional<Task> prepare(W worker, D data);
    }

    protected class WriteDataAction<C extends Data.Change<I,V>> implements ReadActionConsumer<W,D> {
        W worker;
        Transaction transaction;
        DataVersion<I,V> oldDataVersion;
        DataVersion<I,V> newDataVersion;
        D oldData;
        D newData;
        V newVersion;
        WriteActionConsumer<W,D,C> change;
        Message.Values preparedValues;

        public WriteDataAction(WriteActionConsumer<W,D,C> change) { this.change = change; }

        public WriteDataAction(WriteActionConsumer<W,D,C> change, Message.Values preparedValues) { this.change = change; this.preparedValues = preparedValues; }

        public WriteDataAction<C> changeData(WriteActionConsumer<W,D,C> change) throws UserException {
            System.out.println("WriteDataAction.changeData START");
            newDataVersion = newDataVersion(transaction, version -> this.newVersion = version);
            C changeData = (C) oldData.<C>change();
            change.doDataWrite(worker, oldData, changeData, preparedValues);
            newData = changeData.create(newVersion);
            System.out.println("WriteDataAction.changeData END");
            return this;
        }

        public WriteDataAction<C> pushToPersistence() {
            System.out.println("WriteDataAction.pushToPersistence "+newData.getVersion());
            EntityImpl.this.pushToPersistence(worker, newData, newDataVersion, preparedValues);
            return this;
        }

        @Override
        public void doDataRead(W worker, D data) throws UserException {
            this.worker = worker;
            this.oldData = data;
            this.transaction = worker.currentTransaction();
            this.oldDataVersion = getDataVersion();
            changeData(change);
            pushToPersistence();
        }
    }

}
