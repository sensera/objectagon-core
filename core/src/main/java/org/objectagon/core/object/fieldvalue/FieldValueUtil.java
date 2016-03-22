package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldValue;

import java.util.function.Predicate;

/**
 * Created by christian on 2016-03-07.
 */
public class FieldValueUtil {
    public static Predicate<FieldValue.FieldValueIdentity> findField(Field.FieldIdentity fieldIdentity) {
        return fieldValueIdentity -> fieldValueIdentity.getFieldIdentity().equals(fieldIdentity);
    }
}
