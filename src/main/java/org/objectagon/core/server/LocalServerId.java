package org.objectagon.core.server;

import org.objectagon.core.Server;

import java.util.Objects;

/**
 * Created by christian on 2015-11-22.
 */
public class LocalServerId implements Server.ServerId {
    String name;

    public static LocalServerId local(String name) { return new LocalServerId(name);}

    private LocalServerId(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalServerId that = (LocalServerId) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
