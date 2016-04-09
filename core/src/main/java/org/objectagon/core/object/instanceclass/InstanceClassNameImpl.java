package org.objectagon.core.object.instanceclass;

import lombok.Value;
import org.objectagon.core.object.InstanceClass;

/**
 * Created by christian on 2016-04-04.
 */
@Value(staticConstructor = "create")
public class InstanceClassNameImpl implements InstanceClass.InstanceClassName {
    private final String name;
}
