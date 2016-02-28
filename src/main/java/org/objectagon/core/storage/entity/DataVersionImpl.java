package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Converter;
import org.objectagon.core.storage.*;
import org.objectagon.core.storage.data.AbstractData;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by christian on 2016-02-24.
 */
public class DataVersionImpl<I extends Identity, V extends Version> extends AbstractData<I,V> implements DataVersion<I,V> {

    public static <I extends Identity, V extends Version> Builder<I,V> create() { return new DataVersionsBuilder<I,V>();}

    private TreeNode root;

    @Override public Node<V> root() {
        return root;
    }

    public DataVersionImpl(I identity, V version) {
        super(identity, version);
    }

    TreeNode createRoot(V version, NodeType nodeType, Transaction transaction) {
        root = new TreeNode(version, nodeType, transaction);
        return root;
    }


    @Override
    public void convert(Converter.FromData<Data<I, V>> fromData) {

    }

    private class TreeNode implements Node<V> {
        V version;
        NodeType nodeType;
        Transaction transaction;
        List<Node<V>> children = new LinkedList<>();

        @Override public V getVersion() {
            return version;
        }

        @Override public NodeType getType() {
            return nodeType;
        }

        @Override public Transaction getTransaction() {
            return transaction;
        }

        @Override public Stream<Node<V>> getChildren() {
            return children.stream();
        }

        public TreeNode(V version, NodeType nodeType, Transaction transaction) {
            this.version = version;
            this.nodeType = nodeType;
            this.transaction = transaction;
        }

        public TreeNode add(V version, NodeType nodeType, Transaction transaction) {
            TreeNode treeNode = new TreeNode(version, nodeType, transaction);
            children.add(treeNode);
            return treeNode;
        }

    }

    private static class DataVersionsBuilder<I extends Identity, V extends Version> implements Builder<I,V> {
        I identity;
        NodeBuilderImpl rootNodeBuilder;

        @Override
        public void setIdentity(I identity) {
            this.identity = identity;
        }

        @Override
        public DataVersion<I, V> build() {
            if (identity==null)
                throw new NullPointerException("Identity is null!");
            if (rootNodeBuilder ==null)
                throw new NullPointerException("NodeBuilder is null!");
            DataVersionImpl<I, V> ivDataVersions = new DataVersionImpl<>(identity, rootNodeBuilder.version);
            rootNodeBuilder.createRoot(ivDataVersions);
            return ivDataVersions;
        }

        @Override
        public NodeBuilder<V> setRoot(V version, Transaction transaction, NodeType nodeType) {
            rootNodeBuilder = new NodeBuilderImpl(version, nodeType, transaction);
            return rootNodeBuilder;
        }

        private class NodeBuilderImpl implements NodeBuilder<V> {
            V version;
            NodeType nodeType;
            Transaction transaction;
            List<NodeBuilderImpl> children = new LinkedList<>();

            public NodeBuilderImpl(V version, NodeType nodeType, Transaction transaction) {
                this.version = version;
                this.nodeType = nodeType;
                this.transaction = transaction;
            }

            @Override
            public NodeBuilder add(V version, Transaction transaction, NodeType nodeType) {
                NodeBuilderImpl nodeBuilder = new NodeBuilderImpl(version, nodeType, transaction);
                children.add(nodeBuilder);
                return nodeBuilder;
            }

            public void createRoot(DataVersionImpl<I, V> ivDataVersions) {
                DataVersionImpl.TreeNode root = ivDataVersions.createRoot(version, nodeType, transaction);
                children.stream().forEach(nodeBuilder1 -> nodeBuilder1.createNode(root));
            }

            private void createNode(DataVersionImpl.TreeNode treeNode) {
                DataVersionImpl.TreeNode treeNodeChild = treeNode.add(version, nodeType, transaction);
                children.stream().forEach(nodeBuilder1 -> nodeBuilder1.createNode(treeNodeChild));
            }
        }
    }




}
