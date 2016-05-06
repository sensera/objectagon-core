package org.objectagon.core.service;

import lombok.Value;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2016-03-07.
 */

@Value(staticConstructor = "name")
public class StandardServiceName implements Service.ServiceName {
    String name;

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(StandardField.NAME).set(name);
    }
}
