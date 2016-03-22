package org.objectagon.core.object.field.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;
import lombok.Getter;

import java.util.Optional;

/**
 * Created by christian on 2016-03-04.
 */

@Getter
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
}
