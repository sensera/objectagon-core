package org.objectagon.core.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.data.AbstractData;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by christian on 2016-02-24.
 */
public class DataVersionImpl<I extends Identity, V extends Version> extends AbstractData<I,V> implements DataVersion<I,V> {

    public static <I extends Identity, V extends Version> ChangeDataVersion<I,V> create(I identity, NextVersionCounter<V> nextVersionCounter) { return new DataVersionsChange<I,V>(identity, nextVersionCounter);}

    private long versionCounter;
    private NextVersionCounter<V> nextVersionCounter;
    private TransactionVersionNodeImpl root;

    @Override public Optional<TransactionVersionNode<V>> rootNode() {
        return Optional.ofNullable(root);
    }

    public DataVersionImpl(I identity, V version, long versionCounter, NextVersionCounter<V> nextVersionCounter) {
        super(identity, version);
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

    @AllArgsConstructor
    private class TransactionVersionNodeImpl implements TransactionVersionNode<V> {
        @Getter private final V version;
        @Getter private final MergeStrategy mergeStrategy;
        @Getter private final Transaction transaction;
        private final Optional<TransactionVersionNodeImpl> nextVersion;

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
        root.addTo(ivDataVersionsChange);
        return (C) ivDataVersionsChange;
    }


    @RequiredArgsConstructor
    private static class DataVersionsChange<I extends Identity, V extends Version> implements ChangeDataVersion<I,V>, Change<I,V> {
        final private I identity;
        @Setter private long versionCounter = 0;
        final private NextVersionCounter<V> nextVersionCounter;

        private DataVersionsChangeNode root;

        Optional<DataVersionsChangeNode> findNode(Predicate<DataVersionsChangeNode> find) {
            NodeFinder nodeFinder = new NodeFinder(find);
            return nodeFinder.findNodeFromRoot(root);
        }

        @Override
        public ChangeDataVersion<I, V> commit(Transaction transaction) throws UserException {
            preformeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::commit);
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> rollback(Transaction transaction) throws UserException {
            preformeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::rollback);
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> remove(Transaction transaction) throws UserException {
            preformeActionOnNodeSelectedByTransaction(transaction, DataVersionsChangeNode::remove);
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> add(Transaction transaction, V version, MergeStrategy mergeStrategy) throws UserException {
            DataVersionsChangeNode newNode = new DataVersionsChangeNode();
            newNode.setVersion(version);
            newNode.setMergeStrategy(mergeStrategy);

            if (root != null) {
                Optional<DataVersionsChangeNode> lastNodeOption = findNode(DataVersionsChangeNode::hasNextVersion);
                lastNodeOption.ifPresent(addChainedNode(newNode));
                lastNodeOption.orElseThrow(() -> new SevereError(ErrorClass.DATAVERSION, ErrorKind.INCONSISTENCY, MessageValue.address(transaction)));
            } else {
                root = newNode;
            }
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> newVersion(Transaction transaction, Consumer<V> newVersionConsumer) throws UserException {
            V newVersion = nextVersionCounter.nextVersion(versionCounter++);
            preformeActionOnNodeSelectedByTransaction(transaction, node -> node.setVersion(newVersion));
            newVersionConsumer.accept(newVersion);
            return this;
        }

        @Override
        public ChangeDataVersion<I, V> setMergeStrategy(Transaction transaction, MergeStrategy mergeStrategy) throws UserException {
            preformeActionOnNodeSelectedByTransaction(transaction, node -> node.setMergeStrategy(mergeStrategy));
            return this;
        }

        private void preformeActionOnNodeSelectedByTransaction(Transaction transaction, Consumer<DataVersionsChangeNode> commit) {
            Optional<DataVersionsChangeNode> nodeByTransaction = findNode(dataVersionsChangeDataVersionNode -> dataVersionsChangeDataVersionNode.hasTransaction(transaction));
            nodeByTransaction.ifPresent(commit);
            nodeByTransaction.orElseThrow(() -> new SevereError(ErrorClass.DATAVERSION, ErrorKind.TRANSACTION_NOT_FOUND, MessageValue.address(transaction)));
        }

        @Override
        public <D extends org.objectagon.core.storage.Data<I, V>> D create(V version) {
            DataVersionImpl<I, V> ivDataVersion = new DataVersionImpl<>(identity, version, versionCounter, nextVersionCounter);
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
            @Getter @Setter V version;
            @Getter @Setter MergeStrategy mergeStrategy;
            @Getter @Setter Transaction transaction;
            @Getter @Setter Optional<DataVersionsChangeNode> prevVersion;
            @Setter Optional<DataVersionsChangeNode> nextVersion;

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
            }

            public void rollback() {
                prevVersion.ifPresent(dataVersionsChangeDataVersionNode -> version = dataVersionsChangeDataVersionNode.getVersion());
            }

            public void remove() {
                prevVersion.ifPresent(this::removeFromPrevVersion);
                nextVersion.ifPresent(this::removeFromNextVersion);
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

        @lombok.Data
        private class NodeFinder {
            final Predicate<DataVersionsChangeNode> find;

            public Optional<DataVersionsChangeNode> findNodeFromRoot(DataVersionsChangeNode root) {
                Optional<DataVersionsChangeNode> workingWithNode = Optional.of(root);
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
