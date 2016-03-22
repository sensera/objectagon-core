package org.objectagon.core.object.field.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Field;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-03-15.
 */

public class FieldDataChangeImpl implements Field.FieldDataChange {
    final private Field.FieldData fieldData;
    private Field.FieldName name;
    private Field.FieldType type;
    private Optional<Message.Value> defaultValue;

    public FieldDataChangeImpl(Field.FieldData fieldData) {
        this.fieldData = fieldData;
        name = fieldData.getName();
        type = fieldData.getType();
        defaultValue = fieldData.getDefaultValue();
    }

    @Override
    public Field.FieldDataChange setName(Field.FieldName name) {
        this.name = name;
        return this;
    }

    @Override
    public Field.FieldDataChange setType(Field.FieldType type) {
        this.type = type;
        return this;
    }

    @Override
    public <D extends org.objectagon.core.storage.Data<Field.FieldIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D) new FieldDataImpl(
                fieldData.getIdentity(),
                version,
                fieldData.getInstanceClassIdentity(),
                name,
                type,
                defaultValue);
    }


}
