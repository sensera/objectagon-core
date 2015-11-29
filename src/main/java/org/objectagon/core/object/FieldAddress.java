package org.objectagon.core.object;

/**
 * Created by christian on 2015-11-01.
 */
public class FieldAddress extends ObjectAddress {

    public boolean sameField(FieldValueAddress fieldValueAddress) {
        return fieldValueAddress.sameField(this);
    }
}
