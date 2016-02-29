package org.objectagon.core.storage.data;

import lombok.EqualsAndHashCode;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;

/**
 * Created by christian on 2016-02-28.
 */
@EqualsAndHashCode
public class DataKey<I extends Identity, V extends Version> {
    I identity;
    V version;

    public DataKey(I identity, V version) {
        this.identity = identity;
        this.version = version;
    }
}
