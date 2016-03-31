package org.objectagon.core.msg.name;

import lombok.Value;
import org.objectagon.core.msg.Name;

/**
 * Created by christian on 2016-01-06.
 */
@Value(staticConstructor = "name")
public class StandardName implements Name {
    String name;
}
