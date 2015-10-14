package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 2015-10-14.
 */
public class AddressList implements Address {
    private List<Address> addressList = new ArrayList<Address>();

    public AddressList(Address address) {
        add(address);
    }

    public void add(Address address) { addressList.add(address); }
    public void remove(Address address) { addressList.remove(address); }
}
