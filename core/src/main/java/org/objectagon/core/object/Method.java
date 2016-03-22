package org.objectagon.core.object;

import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;

/**
 * Created by christian on 2016-03-06.
 */
public interface Method extends Entity<Method.MethodIdentity,Method.MethodData> {

    Message.Field METHOD_NAME = NamedField.name("METHOD_NAME");
    Message.Field CONSTRUCTOR_NAME = NamedField.name("CONSTRUCTOR_NAME");
    Message.Field CONSTRUCTOR_PARAMS = NamedField.values("CONSTRUCTOR_PARAMS");

    interface MethodIdentity extends Identity, Name.Named {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
    }

    interface MethodData extends Data<MethodIdentity, StandardVersion> {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();

    }
}
