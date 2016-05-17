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
public class DataVersionImpl<I extends Identity, V extends Version> extends AbstractData<I,V> implements DataVersion<I,V> {

    //public static <I extends Identity, V extends Version> ChangeDataVersion<I,V> create(I identity, NextVersionCounter<V> nextVersionCounter) { return new DataVersionsChange<I,V>(identity, nextVersionCounter);}

    public static <I extends Identity, V extends Version> DataVersionImpl<I,V> create(I identity, V version, long versionCounter, NextVersionCounter<V> nextVersionCounter) {
        return new DataVersionImpl<>(identity, version, null, versionCounter, nextVersionCounter);
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

    private DataVersionImpl(I identity, V version, V dataVersion, long versionCounter, NextVersionCounter<V> nextVersionCounter) {
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
        return "DataVersionImpl{" +
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

        public void addTo(DataVersionsChange<I, V> ivDataVersionsChange) {
            DataVersionsChange.DataVersionsChangeNode changeDataVersionNode = ivDataVersionsChange.createRoot();
            transferValuesToBuildNode(changeDataVersionNode, Optional.empty());
        }

        private void transferValuesToBuildNode(DataVersionsChange.DataVersionsChangeNode node, Optional<DataVersionsChange.DataVersionsChangeNode> prev) {
            node.setVersion(version);
            node.setMergeStrategy(mergeStrategy);
            node.setTransaction(transaction);
            node.setPrevVersion(prev);
            nextVersion.ifPresent(vTransactionVersionNode -> vTransactionVersionNode.transferValuesToBuildNode(node.setCreateNext(), Optional.of(node)));
        }
    }

    @Override
    public <C extends Change<I, V>> C change() {
        DataVersionsChange<I, V> ivDataVersionsChange = new DataVersionsChange<>(getIdentity(), nextVersionCounter);
        ivDataVersionsChange.setVersionCounter(versionCounter);
        ivDataVersionsChange.setDataVersion(dataVersion);
        if (root != null)
            root.addTo(ivDataVersionsChange);
        return (C) ivDataVersionsChange;
    }


    //@RequiredArgsConstructor
    private static class DataVersionsChange<I extends Identity, V extends Version> implements ChangeDataVersion<I,V>, Change<I,V> {
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

        public DataVersionsChange(I identity, NextVersionCounter<V> nextVersionCounter) {
            this.identity = identity;
            this.nextVersionCounter = nextVersionCounter;
        }

        @Override
        public ChangeDataVersion<I, V> dataVersion(V dataVersion) throws UserException {
            this.dataVersion = dataVersion;
            return this;
        }

        Optional<DataVersionsChangeNode> findNode(Predicate<DataVersionsChangeNode> find) {
            NodeFinder nodeFinder = new NodeFinder(find);
            return nodeFinder.findNodeFromRoot(root);
        }

        @Override
        public ChangeDataVersion<I, V> commit(Transaction transaction) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::commit);
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> rollback(Transaction transaction) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::rollback);
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> remove(Transaction transaction) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::remove);
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> add(Transaction transaction, Consumer<V> newVersionConsumer, MergeStrategy mergeStrategy) throws UserException {
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
            V newVersion = nextVersionCounter.nextVersion(versionCounter++);
            newVersionConsumer.accept(newVersion);
            return newVersion;
        }

        @Override
        public ChangeDataVersion<I, V> newVersion(Transaction transaction, Consumer<V> newVersionConsumer) throws UserException {
            performeActionOnNodeSelectedByTransaction(transaction, node -> node.setVersion(newVersion(newVersionConsumer)));
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> setMergeStrategy(Transaction transaction, MergeStrategy mergeStrategy) throws UserException {
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
            DataVersionImpl<I, V> ivDataVersion = new DataVersionImpl<>(identity, version, dataVersion, versionCounter, nextVersionCounter);
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

            public void setRoot(DataVersionImpl<I, V> ivDataVersion) {
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
