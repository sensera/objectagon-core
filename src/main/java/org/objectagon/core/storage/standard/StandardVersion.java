package org.objectagon.core.storage.standard;

import org.objectagon.core.storage.Version;

import java.util.Objects;

/**
 * Created by christian on 2015-11-26.
 */
public class StandardVersion implements Version {
    private Long version;

    public StandardVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardVersion that = (StandardVersion) o;
        return Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }
}
