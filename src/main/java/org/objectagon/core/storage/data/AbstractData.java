package org.objectagon.core.storage.data;

import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;

/**
 * Created by christian on 2015-11-01.
 */
public abstract class AbstractData<I extends Identity, V extends Version> implements Data<I,V> {

    private I identity;
    private V version;

    public I getIdentity() {
        return identity;
    }

    public V getVersion() {
        return version;
    }

    public AbstractData(I identity, V version) {
        this.identity = identity;
        this.version = version;
    }
}
