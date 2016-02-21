package org.objectagon.core.storage.standard;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.VolatileNumberValue;
import org.objectagon.core.storage.Version;

import java.util.Objects;

/**
 * Created by christian on 2015-11-26.
 */
public class StandardVersion implements Version {

    public static StandardVersion create(Long version) {
        return new StandardVersion(version);
    }

    public static StandardVersion create(Message.Value value) {
        return new StandardVersion(value.asNumber());
    }

    private Long version;

    public StandardVersion(Long version) {
        this.version = version;
    }

    @Override
    public Message.Value asValue() {
        return VolatileNumberValue.number(Version.VERSION, version);
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
