package org.objectagon.core.storage;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.data.DataType;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-02-26.
 */
public interface DataRevision<I extends Identity, V extends Version> extends Data<I,V> {

    Message.Field DATA_VERSION = NamedField.any("DataRevision");
    Message.Field DATA_VERSION_COUNTER = NamedField.number("DataVersionCounter");

    Optional<TransactionVersionNode<V>> rootNode();
    Optional<V> getDataVersion();

    Type DATA_TYPE = DataType.create("DATA_VERSION");

    default Type getDataType() {return DataRevision.DATA_TYPE;}

    interface TransactionVersionNode<V extends Version> {
        V getVersion();
        MergeStrategy getMergeStrategy();
        Transaction getTransaction();
        Optional<TransactionVersionNode<V>> getNextVersion();
    }

    interface ChangeDataRevision<I extends Identity, V extends Version> extends Change<I,V> {
        ChangeDataRevision<I,V> dataVersion(V dataVersion) throws UserException;
        ChangeDataRevision<I,V> commit(Transaction transaction) throws UserException;
        ChangeDataRevision<I,V> rollback(Transaction transaction) throws UserException;
        ChangeDataRevision<I,V> remove(Transaction transaction) throws UserException;
        ChangeDataRevision<I,V> newVersion(Transaction transaction, Consumer<V> version) throws UserException;
        ChangeDataRevision<I,V> setMergeStrategy(Transaction transaction, MergeStrategy mergeStrategy) throws UserException;
        ChangeDataRevision<I,V> add(Transaction transaction, Consumer<V> newVersionConsumer, MergeStrategy mergeStrategy) throws UserException;
    }

    @FunctionalInterface
    interface NextVersionCounter<V extends Version> {
        V nextVersion(long nextVersionNumber);
    }
}
