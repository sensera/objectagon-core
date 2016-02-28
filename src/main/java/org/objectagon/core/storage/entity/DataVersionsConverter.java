package org.objectagon.core.storage.entity;

import org.objectagon.core.msg.Converter;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValueInterpreter;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.Version;

/**
 * Created by christian on 2016-02-26.
 */
public class DataVersionsConverter<I extends Identity, V extends Version> implements Converter<DataVersion<I,V>> {

    Converter<I> identityConverter;
    Converter<V> versionConverter;

    public DataVersionsConverter(Converter<I> identityConverter, Converter<V> versionConverter) {
        this.identityConverter = identityConverter;
        this.versionConverter = versionConverter;
    }

    @Override
    public ToData<DataVersion<I, V>> toData() {
        return new DataVersionsBuilder()::build;
    }

    @Override
    public FromData<DataVersion<I, V>> fromData() {
        return null;
    }

    private abstract  class AbstractNodeBuilder {

        I identity;
        V version;
        Transaction transaction;
        DataVersion.NodeType nodeType;
        Message.Values children;

        abstract Message.Field childrenNodeField();

        void extractValue(Message.Values values) {
            new MessageValueInterpreter()
                    .add(Identity.IDENTITY,         value -> identity = value.asAddress())
                    .add(Version.VERSION,           value -> version = versionConverter.toData().toData(value))
                    .add(Transaction.TRANSACTION,   value -> transaction = value.asAddress())
                    .add(DataVersion.NODE_TYPE,    value -> nodeType = DataVersion.NodeType.valueOf(value.asText()))
                    .add(childrenNodeField(),    value -> children = value.asValues())
                    .interpret(values);
        }

    }

    private class DataVersionsNodeBuilder extends AbstractNodeBuilder {
        DataVersion.NodeBuilder<V> vNodeBuilder;

        public DataVersionsNodeBuilder(DataVersion.NodeBuilder<V> vNodeBuilder) {
            this.vNodeBuilder = vNodeBuilder;
        }

        @Override
        Message.Field childrenNodeField() {
            return DataVersion.TREE_NODE;
        }

        public void addChild(Message.Value value) {
            DataVersion.TREE_NODE.verifyField(value);
            Message.Values values = value.asValues();

        }
    }

    private class DataVersionsBuilder extends AbstractNodeBuilder {
        DataVersion.Builder<I, V> builder;

        @Override
        Message.Field childrenNodeField() {
            return DataVersion.ROOT_NODE;
        }

        public DataVersionsBuilder() {
            this.builder = DataVersionImpl.create();
        }

        public DataVersion<I, V> build(Message.Value value) {
            DataVersion.DATA_VERSIONS.verifyField(value);

            extractValue(value.asValues());
            builder.setIdentity(identity);
            DataVersion.NodeBuilder<V> vNodeBuilder = builder.setRoot(version, transaction, nodeType);

            DataVersionsNodeBuilder nodeBuilder = new DataVersionsNodeBuilder(vNodeBuilder);
            children.values().forEach(nodeBuilder::addChild);

            return builder.build();
        }

    }


}
