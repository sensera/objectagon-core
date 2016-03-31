package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by christian on 2016-03-27.
 */
public class ServerAliasCtrlImpl implements Server.AliasCtrl {

    Map<Name,Address> addressByName = new HashMap<>();

    @Override
    public void registerAliasForAddress(Name name, Address address) {
        addressByName.put(name, address);
    }

    @Override
    public void removeAlias(Name name) {
        addressByName.remove(name);
    }

    @Override
    public Optional<Address> lookupAddressByAlias(Name name) {
        return Optional.ofNullable(addressByName.get(name));
    }
}
