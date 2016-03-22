package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;

/**
 * Created by christian on 2015-10-18.
 */
public interface FieldValue extends Entity<FieldValue.FieldValueIdentity, FieldValue.FieldValueData> {

    EntityName ENTITY_NAME = EntityName.create("FIELD_VALUE");

    Message.Field VALUE = NamedField.address("VALUE");

    interface FieldValueIdentity extends Identity {
        Instance.InstanceIdentity getInstanceIdentity();
        Field.FieldIdentity getFieldIdentity();
    }

    interface FieldValueData extends Data<FieldValue.FieldValueIdentity, StandardVersion> {
        Instance.InstanceIdentity getInstanceIdentity();
        Field.FieldIdentity getFieldIdentity();
        Message.Value getValue();
    }

    interface ChangeFieldValue extends Data.Change<FieldValue.FieldValueIdentity,StandardVersion> {
        ChangeFieldValue setValue(Message.Value value);
    }


}
