package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;

import java.util.List;

/**
 * Created by christian on 2016-03-07.
 */
public class AddressUtil<A extends Address> {
    List<A> addresses;

    public AddressUtil(List<A> addresses) {
        this.addresses = addresses;
    }


}
