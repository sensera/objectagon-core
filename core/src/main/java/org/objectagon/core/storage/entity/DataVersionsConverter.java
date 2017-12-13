package org.objectagon.core.storage.entity;

/**
 * Created by christian on 2016-02-26.
 */
public class DataVersionsConverter { /*<I extends Identity, V extends Version> implements Converter<DataRevision<I,V>> {

    Converter<I> identityConverter;
    Converter<V> versionConverter;

    public DataVersionsConverter(Converter<I> identityConverter, Converter<V> versionConverter) {
        this.identityConverter = identityConverter;
        this.versionConverter = versionConverter;
    }

    @Override
    public ToData<DataRevision<I, V>> toData() {
        return new DataVersionsBuilder()::build;
    }

    @Override
    public FromData<DataRevision<I, V>> fromData() {
        return null;
    }

    private abstract  class AbstractNodeBuilder {

        I identity;
        V version;
        Transaction transaction;
        DataRevision.NodeType nodeType;
        Message.Values children;

        abstract Message.Field childrenNodeField();

        void extractValue(Message.Values values) {
            new MessageValueInterpreter()
                    .add(Identity.IDENTITY,         value -> identity = value.asAddress())
                    .add(Version.VERSION,           value -> version = versionConverter.toData().toData(value))
                    .add(Transaction.TRANSACTION,   value -> transaction = value.asAddress())
                    .add(DataRevision.NODE_TYPE,    value -> nodeType = DataRevision.NodeType.valueOf(value.asText()))
                    .add(childrenNodeField(),    value -> children = value.asValues())
                    .interpret(values);
        }

    }

    private class DataVersionsNodeBuilder extends AbstractNodeBuilder {
        DataRevision.NodeBuilder<V> vNodeBuilder;

        public DataVersionsNodeBuilder(DataRevision.NodeBuilder<V> vNodeBuilder) {
            this.vNodeBuilder = vNodeBuilder;
        }

        @Override
        Message.Field childrenNodeField() {
            return DataRevision.TREE_NODE;
        }

        public void addChild(Message.Value value) {
            DataRevision.TREE_NODE.verifyField(value);
            Message.Values values = value.asValues();

        }
    }

    private class DataVersionsBuilder extends AbstractNodeBuilder {
        DataRevision.ChangeDataRevision<I, V> changeDataVersion;

        @Override
        Message.Field childrenNodeField() {
            return DataRevision.ROOT_NODE;
        }

        public DataVersionsBuilder() {
            this.changeDataVersion = DataRevisionImpl.create();
        }

        public DataRevision<I, V> build(Message.Value value) {
            DataRevision.DATA_VERSION.verifyField(value);

            extractValue(value.asValues());
            changeDataVersion.setIdentity(identity);
            DataRevision.NodeBuilder<V> vNodeBuilder = changeDataVersion.setRoot(version, transaction);

            DataVersionsNodeBuilder nodeBuilder = new DataVersionsNodeBuilder(vNodeBuilder);
            children.values().forEach(nodeBuilder::addChild);

            return changeDataVersion.build();
        }

    }        */


}
