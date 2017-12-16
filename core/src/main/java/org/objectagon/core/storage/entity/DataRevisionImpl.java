package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.AbstractData;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by christian on 2016-02-24.
 */
public class DataRevisionImpl<I extends Identity, V extends Version> extends AbstractData<I,V> implements DataRevision<I,V> {

    //public static <I extends Identity, V extends Version> ChangeDataRevision<I,V> create(I identity, NextVersionCounter<V> nextVersionCounter) { return new DataRevisionChange<I,V>(identity, nextVersionCounter);}

    public static <I extends Identity, V extends Version> DataRevisionImpl<I,V> create(I identity, V version, long versionCounter, NextVersionCounter<V> nextVersionCounter) {
        return new DataRevisionImpl<>(identity, version, null, versionCounter, nextVersionCounter);
    }

    private long versionCounter;
    private NextVersionCounter<V> nextVersionCounter;
    private V dataVersion;
    private TransactionVersionNodeImpl root;

    @Override public Optional<TransactionVersionNode<V>> rootNode() {
        return Optional.ofNullable(root);
    }

    @Override
    public Optional<V> getDataVersion() {
        return Optional.ofNullable(dataVersion);
    }

    private DataRevisionImpl(I identity, V version, V dataVersion, long versionCounter, NextVersionCounter<V> nextVersionCounter) {
        super(identity, version);
        this.dataVersion = dataVersion;
        this.versionCounter = versionCounter;
        this.nextVersionCounter = nextVersionCounter;
    }

    void setRoot(V version, MergeStrategy mergeStrategy, Transaction transaction, Optional<TransactionVersionNode> nextVersion) {
        root = new TransactionVersionNodeImpl(
                version,
                mergeStrategy,
                transaction,
                nextVersion.map(TransactionVersionNodeImpl::new));
    }

    @Override
    public Iterable<Message.Value> values() {
        //TODO fix values
        return Arrays.asList(MessageValue.number(DATA_VERSION_COUNTER, versionCounter));
    }

    @Override
    public String toString() {
        return "DataRevisionImpl{" +
                "versionCounter=" + versionCounter +
                ", nextVersionCounter=" + nextVersionCounter +
                ", root=" + root +
                '}';
    }

    private class TransactionVersionNodeImpl implements TransactionVersionNode<V> {
        private final V version;
        private final MergeStrategy mergeStrategy;
        private final Transaction transaction;
        private final Optional<TransactionVersionNodeImpl> nextVersion;

        @Override
        public V getVersion() {
            return version;
        }

        @Override
        public MergeStrategy getMergeStrategy() {
            return mergeStrategy;
        }

        @Override
        public Transaction getTransaction() {
            return transaction;
        }

        public TransactionVersionNodeImpl(V version, MergeStrategy mergeStrategy, Transaction transaction, Optional<TransactionVersionNodeImpl> nextVersion) {
            this.version = version;
            this.mergeStrategy = mergeStrategy;
            this.transaction = transaction;
            this.nextVersion = nextVersion;
        }

        @Override
        public Optional<TransactionVersionNode<V>> getNextVersion() {
            return nextVersion.map(transactionVersionNode -> transactionVersionNode);
        }

        public TransactionVersionNodeImpl(TransactionVersionNode<V> transactionVersionNode) {
            version = transactionVersionNode.getVersion();
            mergeStrategy = transactionVersionNode.getMergeStrategy();
            transaction = transactionVersionNode.getTransaction();
            nextVersion = transactionVersionNode.getNextVersion().map(TransactionVersionNodeImpl::new);
        }

        public void addTo(DataRevisionChange<I, V> ivDataRevisionChange) {
            DataRevisionChange.DataVersionsChangeNode changeDataVersionNode = ivDataRevisionChange.createRoot();
            transferValuesToBuildNode(changeDataVersionNode, Optional.empty());
        }

        private void transferValuesToBuildNode(DataRevisionChange.DataVersionsChangeNode node, Optional<DataRevisionChange.DataVersionsChangeNode> prev) {
            node.setVersion(version);
            node.setMergeStrategy(mergeStrategy);
            node.setTransaction(transaction);
            node.setPrevVersion(prev);
            nextVersion.ifPresent(vTransactionVersionNode -> vTransactionVersionNode.transferValuesToBuildNode(node.setCreateNext(), Optional.of(node)));
        }
    }

    @Override
    public <C extends Change<I, V>> C change() {
        DataRevisionChange<I, V> ivDataRevisionChange = new DataRevisionChange<>(getIdentity(), nextVersionCounter);
        ivDataRevisionChange.setVersionCounter(versionCounter);
        ivDataRevisionChange.setDataVersion(dataVersion);
        if (root != null)
            root.addTo(ivDataRevisionChange);
        return (C) ivDataRevisionChange;
    }


    //@RequiredArgsConstructor
    private static class DataRevisionChange<I extends Identity, V extends Version> implements ChangeDataRevision<I,V>, Change<I,V> {
        final private I identity;
        private long versionCounter = 0;
        final private NextVersionCounter<V> nextVersionCounter;
        private V dataVersion;

        private DataVersionsChangeNode root;

        public void setVersionCounter(long versionCounter) {
            this.versionCounter = versionCounter;
        }

        public void setDataVersion(V dataVersion) {
            this.dataVersion = dataVersion;
        }

        public DataRevisionChange(I identity, NextVersionCounter<V> nextVersionCounter) {
            this.identity = identity;
            this.nextVersionCounter = nextVersionCounter;
        }

        @Override
        public ChangeDataRevision<I, V> dataVersion(V dataVersion) throws UserException {
            this.dataVersion = dataVersion;
            return this;
        }

        Optional<DataVersionsChangeNode> findNode(Predicate<DataVersionsChangeNode> find) {
            NodeFinder nodeFinder = new NodeFinder(find);
            return nodeFinder.findNodeFromRoot(root);
        }

        @Override
        public ChangeDataRevision<I, V> commit(Transaction transaction) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::commit);
            return this;
        }

        @Override
        public ChangeDataRevision<I, V> rollback(Transaction transaction) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::rollback);
            return this;
        }

        @Override
        public ChangeDataRevision<I, V> remove(Transaction transaction) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::remove);
            return this;
        }

        @Override
        public ChangeDataRevision<I, V> add(Transaction transaction, Consumer<V> newVersionConsumer, MergeStrategy mergeStrategy) throws UserException {
            DataVersionsChangeNode newNode = new DataVersionsChangeNode();
            newNode.setVersion(newVersion(newVersionConsumer));
            newNode.setMergeStrategy(mergeStrategy);
            newNode.setTransaction(transaction);

            if (root != null) {
                Optional<DataVersionsChangeNode> lastNodeOption = findNode(DataVersionsChangeNode::hasNextVersion);
                lastNodeOption.ifPresent(addChainedNode(newNode));
                lastNodeOption.orElseThrow(() -> new SevereError(ErrorClass.DATAVERSION, ErrorKind.INCONSISTENCY, MessageValue.address(transaction)));
            } else {
                root = newNode;
            }
            return this;
        }

        private V newVersion(Consumer<V> newVersionConsumer) {
            //System.out.println("DataRevisionChange.newVersion !!!!!!!!!!!!!!!! "+versionCounter+" !!!!!!!!!!! "+identity);
            V newVersion = nextVersionCounter.nextVersion(versionCounter++);
            //System.out.println("DataRevisionChange.newVersion newVersion="+newVersion);
            newVersionConsumer.accept(newVersion);
            return newVersion;
        }

        @Override
        public ChangeDataRevision<I, V> newVersion(Transaction transaction, Consumer<V> newVersionConsumer) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, node -> node.setVersion(newVersion(newVersionConsumer)));
            return this;
        }

        @Override
        public ChangeDataRevision<I, V> setMergeStrategy(Transaction transaction, MergeStrategy mergeStrategy) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, node -> node.setMergeStrategy(mergeStrategy));
            return this;
        }

        private void performeActionOnNodeSelectedByTransaction(Transaction transaction, Consumer<DataVersionsChangeNode> commit) {
            Optional<DataVersionsChangeNode> nodeByTransaction = findNode(dataVersionsChangeDataVersionNode -> dataVersionsChangeDataVersionNode.hasTransaction(transaction));
            nodeByTransaction.ifPresent(commit);
            nodeByTransaction.orElseThrow(() -> new SevereError(ErrorClass.DATAVERSION, ErrorKind.TRANSACTION_NOT_FOUND, MessageValue.address(transaction)));
        }

        @Override
        public <D extends org.objectagon.core.storage.Data<I, V>> D create(V version) {
            DataRevisionImpl<I, V> ivDataVersion = new DataRevisionImpl<>(identity, version, dataVersion, versionCounter, nextVersionCounter);
            if (root!=null)
                root.setRoot(ivDataVersion);
            return (D) ivDataVersion;
        }

        DataVersionsChangeNode createRoot() {
            root = new DataVersionsChangeNode();
            return root;
        }

        Consumer<DataVersionsChangeNode> addChainedNode(DataVersionsChangeNode newNode) {
            return targetNode ->  {
                targetNode.setNextVersion(Optional.of(newNode));
                newNode.setPrevVersion(Optional.of(targetNode));
            };
        }

        class DataVersionsChangeNode implements TransactionVersionNode<V> {   //TODO needs refactor extract linked list func into other class and extend from that
            V version;
            MergeStrategy mergeStrategy;
            Transaction transaction;
            Optional<DataVersionsChangeNode> prevVersion = Optional.empty();
            Optional<DataVersionsChangeNode> nextVersion = Optional.empty();
            boolean remove = false;

            @Override
            public V getVersion() {
                return version;
            }

            @Override
            public MergeStrategy getMergeStrategy() {
                return mergeStrategy;
            }

            @Override
            public Transaction getTransaction() {
                return transaction;
            }

            public Optional<DataVersionsChangeNode> getPrevVersion() {
                return prevVersion;
            }

            public boolean isRemove() {
                return remove;
            }

            public void setVersion(V version) {
                this.version = version;
            }

            public void setMergeStrategy(MergeStrategy mergeStrategy) {
                this.mergeStrategy = mergeStrategy;
            }

            public void setTransaction(Transaction transaction) {
                this.transaction = transaction;
            }

            public void setPrevVersion(Optional<DataVersionsChangeNode> prevVersion) {
                this.prevVersion = prevVersion;
            }

            public void setNextVersion(Optional<DataVersionsChangeNode> nextVersion) {
                this.nextVersion = nextVersion;
            }

            boolean hasTransaction(Transaction transaction) {
                return Objects.equals(transaction, this.transaction);
            }

            @Override
            public Optional<TransactionVersionNode<V>> getNextVersion() {
                return nextVersion.map(dataVersionsChangeNode -> dataVersionsChangeNode);
            }

            public DataVersionsChangeNode setCreateNext() {
                nextVersion = Optional.of(new DataVersionsChangeNode());
                return nextVersion.get();
            }

            public void setRoot(DataRevisionImpl<I, V> ivDataVersion) {
                ivDataVersion.setRoot(
                        version,
                        mergeStrategy,
                        transaction,
                        nextVersion.map(dataVersionsChangeNode -> dataVersionsChangeNode));
            }

            public void commit() {
                prevVersion.ifPresent(dataVersionsChangeDataVersionNode -> dataVersionsChangeDataVersionNode.setVersion(version));
                prevVersion.orElseGet(() -> {dataVersion = version;return root = null;} );
            }

            public void rollback() {
                prevVersion.ifPresent(dataVersionsChangeDataVersionNode -> version = dataVersionsChangeDataVersionNode.getVersion());
                prevVersion.orElseGet(() -> root = null);
            }

            public void remove() {
                prevVersion.ifPresent(this::removeFromPrevVersion);
                nextVersion.ifPresent(this::removeFromNextVersion);
                prevVersion.orElseGet(() -> root = null);
            }

            private void removeFromPrevVersion(DataVersionsChangeNode dataVersionsChangeNode) {
                if (!Objects.equals(this.version, dataVersionsChangeNode.version))
                    throw new SevereError(ErrorClass.DATAVERSION, ErrorKind.TRANSACTION_HAS_CHANGES);
                dataVersionsChangeNode.setNextVersion(nextVersion);
            }

            private void removeFromNextVersion(DataVersionsChangeNode dataVersionsChangeNode) {
                dataVersionsChangeNode.setPrevVersion(prevVersion);
            }

            public void add(DataVersionsChangeNode newDataVersionsChangeNode) {
                nextVersion.ifPresent(dataVersionsChangeDataVersionNode -> {
                    dataVersionsChangeDataVersionNode.setPrevVersion(Optional.of(newDataVersionsChangeNode));
                    newDataVersionsChangeNode.setNextVersion(Optional.of(dataVersionsChangeDataVersionNode));
                });
                setNextVersion(Optional.of(newDataVersionsChangeNode));
                newDataVersionsChangeNode.setPrevVersion(Optional.of(this));
            }

            public boolean hasNextVersion() {
                return nextVersion.isPresent();
            }
        }

        private class NodeFinder {
            final Predicate<DataVersionsChangeNode> find;

            public NodeFinder(Predicate<DataVersionsChangeNode> find) {
                this.find = find;
            }

            public Optional<DataVersionsChangeNode> findNodeFromRoot(DataVersionsChangeNode root) {
                Optional<DataVersionsChangeNode> workingWithNode = Optional.ofNullable(root);
                while (workingWithNode.isPresent()) {
                    if (find.test(workingWithNode.get())) {
                        return workingWithNode;
                    }
                    workingWithNode = workingWithNode.get().nextVersion;
                }
                return Optional.empty();
            }

        }
    }



}
