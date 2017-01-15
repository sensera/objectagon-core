package org.objectagon.core.object.field;

import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;

import java.util.function.Predicate;

/**
 * Created by christian on 2016-03-07.
 */
public class FieldUtil {
    Predicate<Field.FieldIdentity> findInstanceClass(InstanceClass.InstanceClassIdentity instanceClassIdentity) {
        return fieldIdentity -> fieldIdentity.getInstanceClassIdentity().equals(instanceClassIdentity);
    }
}
