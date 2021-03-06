package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2015-10-18.
 */
public interface Field extends Entity<Field.FieldIdentity,Field.FieldData> {

    EntityName ENTITY_NAME = EntityName.create("FIELD");

    Message.Field FIELD_IDENTITY = NamedField.address("fieldId");
    Message.Field FIELD_NAME = NamedField.name("fieldName");
    Message.Field FIELD_TYPE = NamedField.name("fieldType");
    Message.Field FIELDS = NamedField.values("fields");
    Message.Field DEFAULT_VALUE = NamedField.any("defaultValue");

    Data.Type DATA_TYPE = DataType.create("FIELD");

    interface FieldName extends Name {}

    interface FieldType extends Name {}

    interface FieldIdentity extends Identity {
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
    }

    interface FieldData extends Data<FieldIdentity, StandardVersion> {
        default Data.Type getDataType() {return DATA_TYPE;}
        InstanceClass.InstanceClassIdentity getInstanceClassIdentity();
        FieldName getName();
        FieldType getType();
        Optional<Message.Value> getDefaultValue();
    }

    interface FieldDataChange extends Data.Change<FieldIdentity,StandardVersion>{
        FieldDataChange setName(FieldName name);
        FieldDataChange setType(FieldType type);
        void setDefaultValue(Message.Value defaultValue);
    }
}
