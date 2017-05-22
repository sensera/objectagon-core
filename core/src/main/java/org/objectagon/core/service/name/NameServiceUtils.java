package org.objectagon.core.service.name;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.task.Task;

import java.util.function.Consumer;

/**
 * Created by christian on 2017-04-26.
 */
public class NameServiceUtils {

    public static Task.SuccessAction storeLookedUpNameIn(Consumer<Address> storeAddress) {
        return (messageName, values) -> storeAddress.accept(MessageValueFieldUtil.create(values).getValueByField(StandardField.ADDRESS).asAddress());
    }
}
