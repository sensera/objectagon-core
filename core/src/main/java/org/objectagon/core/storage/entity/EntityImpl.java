package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.entity.util.DataVersionTransactions;
import org.objectagon.core.storage.persistence.PersistenceService;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;
import org.objectagon.core.utils.Util;

import java.util.*;
import java.util.function.Consumer;


/**
 * Created by christian on 2015-10-17.
 */
public abstract class EntityImpl<I extends Identity, D extends Data<I,V>, V extends Version, W extends EntityWorker>  extends BasicReceiverImpl<I, W> implements Entity<I, D> {

    private long dataVersionCounter = 0;
    private Object dataVersionCounterLock = new Object();
    private DataRevision<I,V> dataRevision;
    private Map<V,D> dataCache = new HashMap<>();
    private Data.Type dataType;

    public DataRevision<I, V> getDataRevision() {
        return dataRevision;
    }

    public EntityImpl(ReceiverCtrl receiverCtrl, Data.Type dataType) {
        super(receiverCtrl);
        this.dataType = dataType;
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
        EntityConfig entityConfig = FindNamedConfiguration.finder(configurations).getConfigurationByName(Entity.ENTITY_CONFIG_NAME);
        dataRevision = entityConfig.getDataVersion(getAddress());
        dataVersionCounter = entityConfig.getDataVersionCounter();
    }

    protected abstract V internalCreateNewVersionForDataVersion(long counterNumber);

    protected abstract D createNewData(Optional<V> version);

    protected Data.Type getDataType() { return dataType; }

    protected V nextDataVersionVersion() {
        final long counterNumber;
        synchronized (dataVersionCounterLock) {
            counterNumber = dataVersionCounter++;
        }
        final V v = internalCreateNewVersionForDataVersion(counterNumber);
        //System.out.println("EntityImpl.nextDataVersionVersion ??????????????? "+v+" for "+getAddress());
        return v;}

    protected Optional<D> getCachedDataByVersion(V version) {
        return Optional.ofNullable(dataCache.get(version));
    }

    protected Optional<D> getCachedDataByTransaction(Transaction transaction) {
        return new NodeFinder(transaction)
                .startFinder(dataRevision.rootNode())
                .map(vNode -> dataCache.get(vNode.getVersion()));
    }

    protected Optional<V> getGetVersionFromTransaction(Transaction transaction) {
        return new NodeFinder(transaction)
                .startFinder(dataRevision.rootNode())
                .map(DataRevision.TransactionVersionNode::getVersion);
    }

    protected Optional<V> getDefaultVersion() {
        return dataRevision.getDataVersion();
    }

    protected void updateDataAndVersion(D newData, DataRevision<I, V> newDataRevision) {
        this.dataRevision = newDataRevision;
        final D removedData = this.dataCache.put(newData.getVersion(), newData);
        if (removedData != null)
            throw new RuntimeException("Internal error! New version number not unique in cache "+newData.getVersion()+" for "+getAddress());
    }

    @Override
    public <VV extends Version> D createNewDataWithVersion(VV versionForNewData) {
        D newData = createNewData(Optional.ofNullable( (V) versionForNewData));
        dataCache.put(newData.getVersion(), newData);
        return newData;
    }

    private class NodeFinder {
        Optional<DataRevision.TransactionVersionNode<V>> res = Optional.empty();
        Transaction transaction;

        public NodeFinder(Transaction transaction) {
            this.transaction = transaction;
        }

        private Optional<DataRevision.TransactionVersionNode<V>> startFinder(Optional<DataRevision.TransactionVersionNode<V>> transactionVersionNodeOption) {
            return transactionVersionNodeOption.isPresent()
                    ? transactionVersionNodeOption.filter(vTransactionVersionNode -> vTransactionVersionNode!=null).map(this::nodeFinder)
                    : Optional.empty();
        }

        private DataRevision.TransactionVersionNode<V> nodeFinder(DataRevision.TransactionVersionNode<V> transactionVersionNode) {
            if (transactionVersionNode==null)
                throw new NullPointerException("transactionVersionNode is null");
            if (Objects.equals(transactionVersionNode.getTransaction(), transaction)) {
                res = Optional.ofNullable(transactionVersionNode);
            } else if (deepEquals(transactionVersionNode.getTransaction(), transaction)) {
                res = Optional.ofNullable(transactionVersionNode);
            } else {
                transactionVersionNode.getNextVersion().ifPresent(this::nodeFinder);
                //transactionVersionNode.getBranchVersion().ifPresent(this::nodeFinder);
            }
            return res.orElseGet(() -> null);
        }

        private boolean deepEquals(Transaction first, Transaction second) {
            if (Objects.equals(first,second))
                return true;
//            if (first.extendsTransaction().map(extendedTrans -> deepEquals(extendedTrans, second)).orElse(false))
//                return true;
            if (second==null)
                return first == null;
            return second.extendsTransaction().map(extendedTrans -> deepEquals(first, extendedTrans)).orElse(false);
        }
    }

    abstract protected D upgrade(D data, DataRevision<I, V> newDataRevision, Transaction transaction);

    private void commit(EntityWorker entityWorker) throws UserException {
        Transaction transaction = entityWorker.currentTransaction();
        V version = getGetVersionFromTransaction(transaction).orElseThrow(() -> new SevereError(ErrorClass.ENTITY, ErrorKind.INCONSISTENCY, MessageValue.address(transaction)));

        DataRevision<I,V> dataRevision = getDataRevision();
        DataRevision<I,V> newDataRevision = dataRevision.<DataRevision.ChangeDataRevision<I,V>>change().commit(transaction).create(null);

        DataRevision.TransactionVersionNode<V> vTransactionVersionNode = new NodeFinder(transaction).startFinder(dataRevision.rootNode()).get();

        switch(vTransactionVersionNode.getMergeStrategy()) {
            case Uppgrade: {
                D oldData = getCachedDataByVersion(version).get(); //TODO Load when not cached
                D newData = upgrade(oldData, newDataRevision, transaction);
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newData, newDataRevision)
                        .addFirstSuccessAction(upgradeDataVersion(newDataRevision, newData))
                        .addSuccessAction(entityWorker::success)
                        .addFailedAction(entityWorker::failed)
                        .start();
                break;
            }
            case OverWrite: {
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newDataRevision)
                        .addFirstSuccessAction(overwriteDataVersion(newDataRevision))
                        .addSuccessAction(entityWorker::success)
                        .addFailedAction(entityWorker::failed)
                        .start();
                break;
            }
            default:
                throw new SevereError(ErrorClass.ENTITY, ErrorKind.NOT_IMPLEMENTED, MessageValue.name(vTransactionVersionNode.getMergeStrategy()));
        }
    }

    private Task.SuccessAction upgradeDataVersion(DataRevision newDataRevision, D newData) {
        return (messageName, values) -> {
            EntityImpl.this.dataRevision = newDataRevision;
            dataCache.put(newData.getVersion(), newData);
        };
    }

    private Task.SuccessAction overwriteDataVersion(DataRevision newDataRevision) {
        return (messageName, values) -> EntityImpl.this.dataRevision = newDataRevision;
    }

    private void rollback(EntityWorker entityWorker) throws UserException{
        Transaction transaction = entityWorker.currentTransaction();
        V version = getGetVersionFromTransaction(transaction).orElseThrow(() -> new SevereError(ErrorClass.ENTITY, ErrorKind.INCONSISTENCY, MessageValue.address(transaction)));

        DataRevision<I,V> dataRevision = getDataRevision();
        DataRevision<I,V> newDataRevision = null;
        newDataRevision = dataRevision.<DataRevision.ChangeDataRevision<I,V>>change().rollback(transaction).create(null);

        DataRevision.TransactionVersionNode<V> vTransactionVersionNode = new NodeFinder(transaction).startFinder(newDataRevision.rootNode()).get();

        switch(vTransactionVersionNode.getMergeStrategy()) {
            case Uppgrade: {
                D oldData = getCachedDataByVersion(version).get(); //TODO Load when not cached
                D newData = upgrade(oldData, newDataRevision, transaction);
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newData, newDataRevision)
                        .addFailedAction(entityWorker::failed)
                        .addSuccessAction(entityWorker::success)
                        .start();
                break;
            }
            case OverWrite: {
                entityWorker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                        .pushData(newDataRevision)
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
                .trigger(EntityProtocol.MessageName.LOCK, this::lockForTransaction)
                .trigger(EntityProtocol.MessageName.UNLOCK, this::unlockForTransaction)
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
                w.createPersistenceServiceProtocolSend(PersistenceService.NAME).pushData(dataRevision.<DataRevision.ChangeDataRevision<I, V>>change()
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
                    w.createPersistenceServiceProtocolSend(PersistenceService.NAME).pushData(dataRevision.<DataRevision.ChangeDataRevision<I, V>>change()
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

    private void lockForTransaction(W w) {
        lock(w, EntityProtocol.DelayCauseName.LOCK_FOR_TRANSACTION, 10L, this::acceptTransactionMessages);
        //lock(w, EntityProtocol.DelayCauseName.LOCK_FOR_TRANSACTION, 10L, this::acceptTransactionMessages);
        w.replyOk();
    }

    private void unlockForTransaction(W w) {
        unlock(w);
        w.replyOk();
    }

    private boolean acceptTransactionMessages(Message.MessageName messageName) {
        final boolean b = EntityProtocol.MessageName.UNLOCK.equals(messageName)
                || EntityProtocol.MessageName.COMMIT.equals(messageName)
                || EntityProtocol.MessageName.ROLLBACK.equals(messageName);
        return b;
    }


    public RunWorker<W> read(ReadActionConsumer<W,D> consumer) {
        return worker -> new ReadDataAction(worker, consumer).execute();
    }

    public <C extends Data.Change<I,V>> RunWorker<W> write(WriteActionConsumer<W,D,C> writeConsumer) {
        return worker -> {
            lock(worker, Entity.DelayCauseName.LOCK_FOR_WRITE, 20L, messageName -> false);
            WriteDataAction<C> writeDataAction = new WriteDataAction<C>(writeConsumer);
            new ReadDataAction(worker, writeDataAction).execute();
        };
    }

    public <C extends Data.Change<I,V>> RunWorker<W> write(WriteActionConsumer<W,D,C> writeConsumer, DataLoadedConsumer<W,D> onDataRead) {
        return worker -> {
            lock(worker, Entity.DelayCauseName.LOCK_FOR_WRITE, 20L, messageName -> false);
            final Transaction transaction = worker.currentTransaction();
            if (!getGetVersionFromTransaction(transaction).isPresent())  {
                worker.trace("No data for transaction", MessageValue.address(getAddress()));
            }

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
            final Optional<V> versionOption = getGetVersionFromTransaction(transaction);

            versionOption.ifPresent(getFromPersistence());
            versionOption.orElseGet(()-> {
                Optional<V> defaultVersion = getDefaultVersion();
                defaultVersion.map(v -> {
                    Optional<D> cachedDataByVersion = getCachedDataByVersion(v);
                    cachedDataByVersion.ifPresent(d -> {
                        try {
                            doDataRead(d);
                        } catch (UserException e) {
                            worker.replyWithError(e);
                        }
                    });
                    cachedDataByVersion.orElseGet(() -> {getFromPersistence().accept(v); return null;});
                    return null;
                });
                defaultVersion.orElseGet(() -> {
                    try {
                        doDataRead(createNewData(Optional.empty()));
                    } catch (UserException e) {
                        worker.replyWithError(e);
                    }
                    return null;
                });
                return null;
            });
        }

        private Consumer<V> getFromPersistence() {
            return (version) -> worker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                    .getData(getDataType(), getAddress(), version)
                    .addFailedAction(worker::failed)
                    .addSuccessAction((messageName, values) -> doDataRead(Util.getValueByField(values, Data.DATA).getValue()))
                    .start();
        }

        private void doDataRead(D data) throws UserException {
            consumer.doDataRead(worker, data);
        }
    }

    protected DataRevision<I, V> newDataVersion(Transaction transaction, Consumer<V> receiveNewVersion) throws UserException {
        DataRevision.ChangeDataRevision<I, V> change = getDataRevision().change();
        if (!DataVersionTransactions.create(getDataRevision()).transactionExists(transaction)) {
            change.add(transaction, receiveNewVersion, Data.MergeStrategy.OverWrite);
        } else
            change.newVersion(transaction, receiveNewVersion);
        return change
                .setMergeStrategy(transaction, Data.MergeStrategy.OverWrite)
                .create(nextDataVersionVersion());
    }

    protected void pushToPersistence(W worker, D newData, DataRevision<I, V> newDataRevision, Message.Values preparedValues) {
        worker.createPersistenceServiceProtocolSend(PersistenceService.NAME)
                .pushData(newData, newDataRevision)
                .addFailedAction(worker::failed)
                .addSuccessAction((messageName, values) -> {
                    try {
                        updateDataAndVersion(newData, newDataRevision);
                        worker.success(messageName, preparedValues!=null ? preparedValues.values():values);
                    } catch (Exception e) {
                        worker.failed(ErrorClass.ENTITY, ErrorKind.UNEXPECTED, Arrays.asList(MessageValue.exception(e)));
                    } finally {
                        unlock(worker);
                    }

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
        DataRevision<I,V> oldDataRevision;
        DataRevision<I,V> newDataRevision;
        D oldData;
        D newData;
        V newVersion;
        WriteActionConsumer<W,D,C> change;
        Message.Values preparedValues;

        public WriteDataAction(WriteActionConsumer<W,D,C> change) { this.change = change; }

        public WriteDataAction(WriteActionConsumer<W,D,C> change, Message.Values preparedValues) { this.change = change; this.preparedValues = preparedValues; }

        public WriteDataAction<C> changeData(WriteActionConsumer<W,D,C> change) throws UserException {
            //System.out.println("WriteDataAction.changeData START");
            newDataRevision = newDataVersion(transaction, version -> this.newVersion = version);
            C changeData = (C) oldData.<C>change();
            change.doDataWrite(worker, oldData, changeData, getValues());
            newData = changeData.create(newVersion);
            //System.out.println("WriteDataAction.changeData END");
            return this;
        }

        private Message.Values getValues() {
            if (preparedValues==null)
                return () -> worker.getValues();
            return preparedValues;
        }

        public WriteDataAction<C> pushToPersistence() {
            //System.out.println("WriteDataAction.pushToPersistence "+newData.getVersion());
            EntityImpl.this.pushToPersistence(worker, newData, newDataRevision, getValues());
            return this;
        }

        @Override
        public void doDataRead(W worker, D data) throws UserException {
            this.worker = worker;
            this.oldData = data;
            this.transaction = worker.currentTransaction();
            this.oldDataRevision = getDataRevision();
            changeData(change);
            pushToPersistence();
        }
    }

}
