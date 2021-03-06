package org.objectagon.core.object.field.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by christian on 2016-03-04.
 */

public class FieldDataImpl extends AbstractData<Field.FieldIdentity, StandardVersion> implements Field.FieldData {

    public static FieldDataImpl create(Field.FieldIdentity identity,
                                                         StandardVersion version,
                                                         Field.FieldName name,
                                                         Field.FieldType type) {
        return new FieldDataImpl(identity, version, identity.getInstanceClassIdentity(), name, type, Optional.empty());
    }

    final private InstanceClass.InstanceClassIdentity instanceClassIdentity;
    final private Field.FieldName name;
    final private Field.FieldType type;
    final private Optional<Message.Value> defaultValue;

    @Override
    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
        return instanceClassIdentity;
    }

    @Override
    public Field.FieldName getName() {
        return name;
    }

    @Override
    public Field.FieldType getType() {
        return type;
    }

    @Override
    public Optional<Message.Value> getDefaultValue() {
        return defaultValue;
    }

    FieldDataImpl(
            Field.FieldIdentity identity,
            StandardVersion version,
            InstanceClass.InstanceClassIdentity instanceClassIdentity,
            Field.FieldName name,
            Field.FieldType type,
            Optional<Message.Value> defaultValue) {
        super(identity, version);
        this.instanceClassIdentity = instanceClassIdentity;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public <C extends Change<Field.FieldIdentity, StandardVersion>> C change() {
        return (C) new FieldDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        //TODO Fix
        return Arrays.asList(
                MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, instanceClassIdentity),
                MessageValue.name(Field.FIELD_NAME, name),
                MessageValue.name(Field.FIELD_TYPE, type)
        );
    }
}
