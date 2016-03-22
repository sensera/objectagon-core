package org.objectagon.core.service;

import lombok.Value;

/**
 * Created by christian on 2016-03-07.
 */

@Value(staticConstructor = "name")
public class StandardServiceName implements Service.ServiceName {
    String name;
}
