package org.objectagon.core.utils;

import lombok.Data;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-03.
 */
@Data(staticConstructor = "finder")
public class FindNamedConfiguration {
    private final Receiver.Configurations[] configurations;

    public <C extends Receiver.NamedConfiguration> C getConfigurationByName(Name name) {
        Optional<Receiver.NamedConfiguration> opt = Stream.of(configurations).map(configurations -> configurations.getConfigurationByName(name)).findAny().get();
        return (C) opt.get();
    }

    public <A extends Address> A createConfiguredAddress(CreateAddress<A> createAddress) {
        Receiver.AddressConfigurationParameters addressConfigurationParameters = getConfigurationByName(Receiver.ADDRESS_CONFIGURATIONS);
        return createAddress.create(addressConfigurationParameters.getServerId(), addressConfigurationParameters.getTimeStamp(), addressConfigurationParameters.getId());
    }

    @FunctionalInterface
    public interface CreateAddress<A extends Address> {
        A create(Server.ServerId serverId, long timestamp, long addressId);
    }
}
