package org.objectagon.core.object.instance.data;

import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.objectagon.core.msg.address.AddressList.createFromSteamAndChanges;

/**
 * Created by christian on 2016-03-15.
 */
public class InstanceDataChangeImpl implements Instance.InstanceDataChange {
    Instance.InstanceData instanceData;
    List<Consumer<List<FieldValue.FieldValueIdentity>>> fieldValuesChanges = new LinkedList<>();
    List<Consumer<List<Relation.RelationIdentity>>> relationsChanges = new LinkedList<>();

    public InstanceDataChangeImpl(Instance.InstanceData instanceData) {
        this.instanceData = instanceData;
    }

    @Override
    public Instance.InstanceDataChange addField(FieldValue.FieldValueIdentity fieldValueIdentity) {
        fieldValuesChanges.add(fieldValueIdentities -> fieldValueIdentities.add(fieldValueIdentity));
        return this;
    }

    @Override
    public Instance.InstanceDataChange removeField(FieldValue.FieldValueIdentity fieldValueIdentity) {
        fieldValuesChanges.add(fieldValueIdentities -> fieldValueIdentities.remove(fieldValueIdentity));
        return this;
    }

    @Override
    public Instance.InstanceDataChange addRelation(Relation.RelationIdentity relationIdentity) {
        relationsChanges.add(relationIdentities -> relationIdentities.add(relationIdentity));
        return this;
    }

    @Override
    public Instance.InstanceDataChange removeRelation(Relation.RelationIdentity relationIdentity) {
        relationsChanges.add(relationIdentities -> relationIdentities.remove(relationIdentity));
        return this;
    }

    @Override
    public <D extends Data<Instance.InstanceIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D) new InstanceDataImpl(
                instanceData.getIdentity(),
                version,
                instanceData.getInstanceClassIdentity(),
                createFromSteamAndChanges(instanceData.getFieldValues(), fieldValuesChanges),
                createFromSteamAndChanges(instanceData.getRelations(), relationsChanges)
        );
    }


}
