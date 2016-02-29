package org.objectagon.core.storage;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-02-26.
 */
public interface DataVersion<I extends Identity, V extends Version> extends Data<I,V> {

    Message.Field DATA_VERSIONS = NamedField.values("DataVersion");

    // Will be removed
    Message.Field ROOT_NODE = NamedField.values("RootNode");
    Message.Field NODE_TYPE = NamedField.name("NodeType");
    Message.Field TREE_NODE = NamedField.values("TreeNode");

    enum NodeType { Main, Branch }

    I getIdentity();
    Node<V> root();

    interface Node<V extends Version> {
        V getVersion();
        NodeType getType();
        Transaction getTransaction();
        Stream<Node<V>> getChildren();
    }

    interface Builder<I extends Identity, V extends Version> {
        void setIdentity(I identity);
        NodeBuilder<V> setRoot(V version, Transaction transaction, NodeType nodeType);
        DataVersion<I,V> build();
    }

    interface NodeBuilder<V extends Version> {
        NodeBuilder add(V version, Transaction transaction, NodeType nodeType);
    }

    interface ChangeDataVersion<I extends Identity, V extends Version> extends Change<I,V> {

    }
}
