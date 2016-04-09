package org.objectagon.core.storage.data;

import lombok.Value;
import org.objectagon.core.storage.Data;

/**
 * Created by christian on 2016-04-05.
 */
@Value(staticConstructor = "create")
public class DataType implements Data.Type {
    String name;
}
