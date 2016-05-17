package org.objectagon.core.service;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;

import java.util.Objects;

/**
 * Created by christian on 2016-03-07.
 */

public class StandardServiceNameAddress extends StandardAddress implements Service.ServiceName {
    Service.ServiceName name;

    @Override
    public String getName() {
        return name.getName();
    }

    public static StandardServiceNameAddress name(Service.ServiceName name, Server.ServerId serverId, long timestamp, long addressId) {
        return new StandardServiceNameAddress(name, serverId, timestamp, addressId);
    }

    protected StandardServiceNameAddress(Service.ServiceName name, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StandardServiceNameAddress)) {
            if (o instanceof Service.ServiceName) {
                Service.ServiceName that = (Service.ServiceName) o;
                return Objects.equals(that, name);
            }
            return false;
        }
        if (!super.equals(o)) return false;
        StandardServiceNameAddress that = (StandardServiceNameAddress) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
        return "StandardServiceNameAddress{" +
                "name=" + name +
                "} " + super.toString();
    }
}
