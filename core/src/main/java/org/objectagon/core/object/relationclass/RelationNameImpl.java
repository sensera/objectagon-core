package org.objectagon.core.object.relationclass;

import lombok.Value;
import org.objectagon.core.object.RelationClass;

/**
 * Created by christian on 2016-04-04.
 */
@Value(staticConstructor = "create")
public class RelationNameImpl implements RelationClass.RelationName {
    private final String name;
}
