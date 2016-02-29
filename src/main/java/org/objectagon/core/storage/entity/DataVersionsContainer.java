package org.objectagon.core.storage.entity;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Version;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by christian on 2015-11-01.
 */
public class DataVersionsContainer<D extends Data, V extends Version>  {
    DataVersion<>
    DataChain chain;

    public DataVersionsContainer(D data) {
        this.data = data;
    }

    public DataVersionsContainer(Iterable<D> data) {
        Iterator<D> dataIterator = data.iterator();
        this.data = dataIterator.next();
        if (dataIterator.hasNext())
            chain = new DataChain(null, dataIterator);
    }

    public void commit(final V version) {
        if (data.getVersion().equals(version)) {
            //What is commit root? Do nothing?
            return;
        }
        if (chain==null)
            throw new SevereError(ErrorClass.STORAGE, ErrorKind.MISSING_VERSION_IN_DATA_VERSIONS);
        if (chain.equalsVersion(version)) {
            data = chain.data;
            chain.remove();
        }

        Optional<DataChain> target = chain.findVersion(version);
        if (!target.isPresent())
            throw new SevereError(ErrorClass.STORAGE, ErrorKind.MISSING_VERSION_IN_DATA_VERSIONS);

        target.get().commit();
    }

    public D getDataByVersion(V version) {
        Optional<DataChain> target = chain.findVersion(version);
        if (!target.isPresent())
            throw new SevereError(ErrorClass.STORAGE, ErrorKind.MISSING_VERSION_IN_DATA_VERSIONS);
        return target.get().data;
    }

    public void rollback(V version) {


    }

    private class DataChain {
        DataChain prev;
        DataChain next;
        D data;

        public DataChain(DataChain prev, D data) {
            this.prev = prev;
            this.data = data;
        }

        public DataChain(DataChain prev, Iterator<D> dataIterator) {
            this.prev = prev;
            data = dataIterator.next();
            if (dataIterator.hasNext())
                next = new DataChain(this, dataIterator);
        }

        public boolean equalsVersion(V version) {
            return data.getVersion().equals(version);
        }

        public Optional<DataChain> findVersion(V version) {
            if (equalsVersion(version))
                return Optional.of(this);
            if (next==null)
                return Optional.empty();
            return next.findVersion(version);
        }

        public void remove() {
            if (prev != null)
                prev.next = next;
            if (next != null)
                next.prev = prev;
            prev = null;
            next = null;
            data = null;
        }

        public void commit() {
            if (prev==null)
                throw new SevereError(ErrorClass.STORAGE, ErrorKind.MISSING_VERSION_IN_DATA_VERSIONS);
            prev.data = data;
            remove();
        }
    }
}
