package org.objectagon.core.rest2.service;

import org.objectagon.core.msg.Name;

import java.util.Objects;

/**
 * Created by christian on 2017-03-17.
 */
public class RestSessionToken implements Name {
    String name;

    private RestSessionToken(String name) {
        this.name = name;
    }

    public static RestSessionToken name(String name) {
        return new RestSessionToken(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestSessionToken)) return false;
        RestSessionToken that = (RestSessionToken) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
