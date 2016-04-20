package org.objectagon.core.object.fieldvalue.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-04-18.
 */
@lombok.Data
public class ChangeFieldValueImpl implements FieldValue.ChangeFieldValue {
    private final FieldValue.FieldValueData fieldValueData;
    private Optional<Message.Value> value = Optional.empty();

    @Override
    public FieldValue.ChangeFieldValue setValue(Message.Value value) {
        this.value = Optional.ofNullable(value);
        return this;
    }

    @Override
    public <D extends Data<FieldValue.FieldValueIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D) new FieldValueDataImpl(
                fieldValueData.getInstanceIdentity(),
                fieldValueData.getFieldIdentity(),
                fieldValueData.getIdentity(),
                version,
                value.orElse(fieldValueData.getValue())
        );
    }
}
