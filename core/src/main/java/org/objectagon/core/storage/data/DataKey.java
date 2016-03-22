package org.objectagon.core.storage.data;

import org.objectagon.core.storage.Version;
import lombok.EqualsAndHashCode;
import org.objectagon.core.storage.Identity;

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
