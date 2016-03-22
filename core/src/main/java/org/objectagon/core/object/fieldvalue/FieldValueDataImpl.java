package org.objectagon.core.object.fieldvalue;

import lombok.Getter;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.Instance;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-03-04.
 */
@Getter
class FieldValueDataImpl extends AbstractData<FieldValue.FieldValueIdentity, StandardVersion> implements FieldValue.FieldValueData {

    public static FieldValue.FieldValueData create(FieldValue.FieldValueIdentity fieldValueIdentity, StandardVersion standardVersion) {
        return new FieldValueDataImpl(
                fieldValueIdentity.getInstanceIdentity(),
                fieldValueIdentity.getFieldIdentity(),
                fieldValueIdentity, standardVersion,
                MessageValue.empty());
    }

    private Instance.InstanceIdentity instanceIdentity;
    private Field.FieldIdentity fieldIdentity;
    private Message.Value value;

    private FieldValueDataImpl(
            Instance.InstanceIdentity instanceIdentity,
            Field.FieldIdentity fieldIdentity,
            FieldValue.FieldValueIdentity identity,
            StandardVersion version,
            Message.Value value) {
        super(identity, version);
        this.instanceIdentity = instanceIdentity;
        this.fieldIdentity = fieldIdentity;
        this.value = value;
    }

    @Override
    public <C extends Change<FieldValue.FieldValueIdentity, StandardVersion>> C change() {
        return null;
    }


}
