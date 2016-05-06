package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by christian on 2015-10-14.
 */

public class AddressList<A extends Address> implements Address {
    private List<A> addressList = new ArrayList<>();

    public AddressList() {}

    protected AddressList(List<A> addressList) { this.addressList = addressList;}

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

    public Stream<A> stream() {
        return addressList.stream();
    }

    public static <A extends Address> AddressList<A> empty() {
        return new AddressList<>();
    }

    public static <A extends Address> AddressList<A> createFromSteamAndChanges(Stream<A> fieldValues, List<Consumer<List<A>>> changes) {
        List<A> list = fieldValues.collect(toList());
        changes.stream().forEach(listConsumer -> listConsumer.accept(list));
        return new AddressList<>(list);
    }

    public static <A extends Address> AddressList<A> createFromSteam(Stream<A> fieldValues) {
        return new AddressList<>(fieldValues.collect(toList()));
    }

    public int size() {
        return addressList.size();
    }

    @FunctionalInterface
    public interface SelectAddress<A extends Address> {
        boolean equals(A address);
    }

    @Override
    public String toString() {
        return "AddressList["+addressList+"]";
    }

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        addressList.stream().forEach(a -> a.toValue(builderItem.values(StandardField.ADDRESS)));
    }
}
