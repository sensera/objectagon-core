package org.objectagon.core.object.instanceclass.data;

import org.objectagon.core.msg.Name;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2016-03-16.
 */
public class ChangeInstanceClassImpl implements InstanceClass.ChangeInstanceClass {

    private InstanceClass.InstanceClassData instanceClassData;
    private Stream<Map.Entry<Name, Instance.InstanceIdentity>> instanceAliases;
    private List<Consumer<List<Field.FieldIdentity>>> fieldChanges = new ArrayList<>();
    private List<Consumer<List<RelationClass.RelationClassIdentity>>> relationClassChanges = new ArrayList<>();
    private List<Consumer<Map<Name, Instance.InstanceIdentity>>> instanceAliasChanges = new ArrayList<>();
    private Optional<InstanceClass.InstanceClassName> name = Optional.empty();

    public ChangeInstanceClassImpl(InstanceClass.InstanceClassData instanceClassData, Stream<Map.Entry<Name, Instance.InstanceIdentity>> instanceAliases) {
        this.instanceClassData = instanceClassData;
        this.instanceAliases = instanceAliases;
    }

    @Override
    public InstanceClass.ChangeInstanceClass addField(Field.FieldIdentity fieldIdentity) {
        fieldChanges.add(fieldIdentities -> fieldIdentities.add(fieldIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass addRelation(RelationClass.RelationClassIdentity relationClassIdentity) {
        relationClassChanges.add(relationClassIdentities -> relationClassIdentities.add(relationClassIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass removeField(Field.FieldIdentity fieldIdentity) {
        fieldChanges.add(fieldIdentities -> fieldIdentities.remove(fieldIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass removeRelation(RelationClass.RelationClassIdentity relationClassIdentity) {
        relationClassChanges.add(relationClassIdentities -> relationClassIdentities.remove(relationClassIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass setName(InstanceClass.InstanceClassName name) {
        this.name = Optional.of(name);
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass addInstanceAlias(Instance.InstanceIdentity instanceIdentity, Name alias) {
        instanceAliasChanges.add(nameInstanceIdentityMap -> nameInstanceIdentityMap.put(alias, instanceIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass removeInstanceAlias(Name alias) {
        instanceAliasChanges.add(nameInstanceIdentityMap -> nameInstanceIdentityMap.remove(alias));
        return this;
    }

    @Override
    public <D extends Data<InstanceClass.InstanceClassIdentity, StandardVersion>> D create(StandardVersion version) {
        List<Field.FieldIdentity> newFields = instanceClassData.getFields().collect(Collectors.toList());
        fieldChanges.stream().forEach(listConsumer -> listConsumer.accept(newFields));
        List<RelationClass.RelationClassIdentity> newRelations = instanceClassData.getRelations().collect(Collectors.toList());
        relationClassChanges.stream().forEach(listConsumer -> listConsumer.accept(newRelations));
        if (!instanceAliasChanges.isEmpty()) {
            Map<Name,Instance.InstanceIdentity> newInstanceAliases = new HashMap<>();
            instanceAliases.forEach(nameInstanceIdentityEntry -> newInstanceAliases.put(nameInstanceIdentityEntry.getKey(), nameInstanceIdentityEntry.getValue()));
            instanceAliasChanges.forEach(mapConsumer -> mapConsumer.accept(newInstanceAliases));
            instanceAliases = newInstanceAliases.entrySet().stream();
        }
        InstanceClassDataImpl res = new InstanceClassDataImpl(
                name.orElse(instanceClassData.getName()),
                newFields.stream(),
                newRelations.stream(),
                instanceAliases,
                instanceClassData.getIdentity(),
                version);
        return (D) res;
    }
}
