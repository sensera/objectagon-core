package org.objectagon.core.server;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;

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
        if (addressByName.put(name, address)!=null)
            throw new SevereError(ErrorClass.SERVER, ErrorKind.NAME_ALLREADY_REGISTERED, MessageValue.name(name));
    }

    @Override
    public void removeAlias(Name name) {
        addressByName.remove(name);
    }

    @Override
    public Optional<Address> lookupAddressByAlias(Name name) {
        Optional<Address> address = Optional.ofNullable(addressByName.get(name));
        if (!address.isPresent()) {
            System.out.println("ServerAliasCtrlImpl.lookupAddressByAlias ******************** NOT FOUND "+name+" *******************************");
            addressByName.keySet().forEach(System.out::println);
            System.out.println("ServerAliasCtrlImpl.lookupAddressByAlias ******************** NOT FOUND "+name+" *******************************");
            new Exception().printStackTrace();
            return Optional.empty();
        }
        return address;
    }
}
