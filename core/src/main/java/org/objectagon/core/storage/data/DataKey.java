package org.objectagon.core.storage.data;

import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;

import java.util.Objects;

/**
 * Created by christian on 2016-02-28.
 */
public class DataKey<I extends Identity, V extends Version> {
    I identity;
    V version;

    public DataKey(I identity, V version) {
        this.identity = identity;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataKey)) return false;
        DataKey<?, ?> dataKey = (DataKey<?, ?>) o;
        return Objects.equals(identity, dataKey.identity) &&
                Objects.equals(version, dataKey.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, version);
    }

    @Override
    public String toString() {
        return "DataKey{" +
                "identity=" + identity +
                ", version=" + version +
                '}';
    }
}
