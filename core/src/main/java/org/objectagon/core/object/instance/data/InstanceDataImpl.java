package org.objectagon.core.object.instance.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.*;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceDataImpl extends AbstractData<Instance.InstanceIdentity, StandardVersion> implements Instance.InstanceData {

    public static InstanceDataImpl create(Instance.InstanceIdentity identity, StandardVersion version) {
        return new InstanceDataImpl(identity, version, identity.getInstanceClassIdentity(), AddressList.empty(), AddressList.empty());
    }

    final private InstanceClass.InstanceClassIdentity instanceClassIdentity;
    final private AddressList<FieldValue.FieldValueIdentity> values;
    final private AddressList<Relation.RelationIdentity> relations;

    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity() {
        return instanceClassIdentity;
    }

    @Override public Stream<FieldValue.FieldValueIdentity> getFieldValues() {
        return values.stream();
    }
    @Override public Stream<Relation.RelationIdentity> getRelations() {
        return relations.stream();
    }

    InstanceDataImpl(
            Instance.InstanceIdentity identity,
            StandardVersion version,
            InstanceClass.InstanceClassIdentity instanceClassIdentity,
            AddressList<FieldValue.FieldValueIdentity> values,
            AddressList<Relation.RelationIdentity> relations) {
        super(identity, version);
        if (instanceClassIdentity==null)
            throw new NullPointerException("instanceClassIdentity is null");
        this.instanceClassIdentity = instanceClassIdentity;
        this.values = values;
        this.relations = relations;
    }

    @Override
    public <C extends Change<Instance.InstanceIdentity, StandardVersion>> C change() {
        return (C) new InstanceDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        //TODO Fix
        return Arrays.asList(
                MessageValue.address(InstanceClass.INSTANCE_CLASS_IDENTITY, instanceClassIdentity)
        );
    }

    @Override
    public String toString() {
        return "InstanceDataImpl{" +
                "instanceClassIdentity=" + instanceClassIdentity +
                ", values=" + values +
                ", relations=" + relations +
                '}';
    }
}
