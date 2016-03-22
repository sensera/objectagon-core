package org.objectagon.core.object.field;

import lombok.Value;
import org.objectagon.core.object.Field;

/**
 * Created by christian on 2016-03-19.
 */
@Value(staticConstructor = "create")
public class FieldNameImpl implements Field.FieldName {
    String name;
}
