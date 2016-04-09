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
public interface DataVersion<I extends Identity, V extends Version> extends Data<I,V> {

    Message.Field DATA_VERSION = NamedField.any("DataVersion");
    Message.Field DATA_VERSION_COUNTER = NamedField.number("DataVersionCounter");

    Optional<TransactionVersionNode<V>> rootNode();

    Type DATA_TYPE = DataType.create("DATA_VERSION");

    default Type getDataType() {return DataVersion.DATA_TYPE;}

    interface TransactionVersionNode<V extends Version> {
        V getVersion();
        MergeStrategy getMergeStrategy();
        Transaction getTransaction();
        Optional<TransactionVersionNode<V>> getNextVersion();
    }

    interface ChangeDataVersion<I extends Identity, V extends Version> extends Change<I,V> {
        ChangeDataVersion<I,V> commit(Transaction transaction) throws UserException;
        ChangeDataVersion<I,V> rollback(Transaction transaction) throws UserException;
        ChangeDataVersion<I,V> remove(Transaction transaction) throws UserException;
        ChangeDataVersion<I,V> newVersion(Transaction transaction, Consumer<V> version) throws UserException;
        ChangeDataVersion<I,V> setMergeStrategy(Transaction transaction, MergeStrategy mergeStrategy) throws UserException;
        ChangeDataVersion<I,V> add(Transaction transaction, Consumer<V> newVersionConsumer, MergeStrategy mergeStrategy) throws UserException;
    }

    @FunctionalInterface
    interface NextVersionCounter<V extends Version> {
        V nextVersion(long nextVersionNumber);
    }
}
