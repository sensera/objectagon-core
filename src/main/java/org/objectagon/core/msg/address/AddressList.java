package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 2015-10-14.
 */
public class AddressList<A extends Address> implements Address {
    private List<A> addressList = new ArrayList<A>();

    public AddressList() {}

    public AddressList(A address) {add(address);}

    public void add(A address) { addressList.add(address); }
    public void remove(A address) { addressList.remove(address); }

    public boolean isEmpty() {
        return addressList.isEmpty();
    }
}
