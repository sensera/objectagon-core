package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;
import sun.java2d.pipe.AAShapePipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by christian on 2015-10-14.
 */

public class AddressList<A extends Address> implements Address {
    private List<A> addressList = new ArrayList<>();

    public AddressList() {}

    public AddressList(A address) {add(address);}

    public void add(A address) { addressList.add(address); }
    public void remove(A address) { addressList.remove(address); }

    public boolean isEmpty() {
        return addressList.isEmpty();
    }

    public Optional<A> select(SelectAddress<A> selectAddress) {
        for (A address : addressList)
            if (selectAddress.equals(address))
                return Optional.of(address);
        return Optional.empty();
    }

    @FunctionalInterface
    public interface SelectAddress<A extends Address> {
        boolean equals(A address);
    }
}
