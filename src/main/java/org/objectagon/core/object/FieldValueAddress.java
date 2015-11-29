package org.objectagon.core.object;

import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2015-10-20.
 */
public class FieldValueAddress implements Address {
    FieldAddress fieldAddress;

    public boolean sameField(FieldAddress fieldAddress) {
        return fieldAddress.equals(fieldAddress);
    }

    public boolean sameField(FieldValueAddress fieldValueAddress) {
        return fieldValueAddress.sameField(fieldAddress);
    }
}
