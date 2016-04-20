package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-10-18.
 */
public interface FieldValue extends Entity<FieldValue.FieldValueIdentity, FieldValue.FieldValueData> {

    EntityName ENTITY_NAME = EntityName.create("FIELD_VALUE");

    Message.Field FIELD_VALUE_IDENTITY = NamedField.address("FIELD_VALUE_IDENTITY");
    Message.Field VALUE = NamedField.address("VALUE");

    Data.Type DATA_TYPE = DataType.create("FIELD_VALUE");

    interface FieldValueIdentity extends Identity {
        Instance.InstanceIdentity getInstanceIdentity();
        Field.FieldIdentity getFieldIdentity();
    }

    interface FieldValueData extends Data<FieldValue.FieldValueIdentity, StandardVersion> {
        default Type getDataType() {return DATA_TYPE;}

        Instance.InstanceIdentity getInstanceIdentity();
        Field.FieldIdentity getFieldIdentity();
        Message.Value getValue();
    }

    interface ChangeFieldValue extends Data.Change<FieldValue.FieldValueIdentity,StandardVersion> {
        ChangeFieldValue setValue(Message.Value value);
    }


}
