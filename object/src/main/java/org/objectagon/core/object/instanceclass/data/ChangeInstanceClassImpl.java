package org.objectagon.core.object.instanceclass.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.object.*;
import org.objectagon.core.object.instanceclass.MethodClassImpl;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.Util;

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
    private List<Consumer<List<InstanceClass.MethodClass>>> methodChanges = new ArrayList<>();
    private List<Consumer<Map<Name, Instance.InstanceIdentity>>> instanceAliasChanges = new ArrayList<>();
    private Optional<InstanceClass.InstanceClassName> name = Optional.empty();

    public ChangeInstanceClassImpl(InstanceClass.InstanceClassData instanceClassData, Stream<Map.Entry<Name, Instance.InstanceIdentity>> instanceAliases) {
        this.instanceClassData = instanceClassData;
        this.instanceAliases = instanceAliases;
    }

    @Override
    public InstanceClass.ChangeInstanceClass addField(Field.FieldIdentity fieldIdentity) {
        if (fieldIdentity==null)
            throw new NullPointerException("fieldIdentity is null!");
        fieldChanges.add(fieldIdentities -> fieldIdentities.add(fieldIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass addRelation(RelationClass.RelationClassIdentity relationClassIdentity) {
        if (relationClassIdentity==null)
            throw new NullPointerException("relationClassIdentity is null!");
        relationClassChanges.add(relationClassIdentities -> relationClassIdentities.add(relationClassIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass removeField(Field.FieldIdentity fieldIdentity) {
        if (fieldIdentity==null)
            throw new NullPointerException("fieldIdentity is null!");
        fieldChanges.add(fieldIdentities -> fieldIdentities.remove(fieldIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass removeRelation(RelationClass.RelationClassIdentity relationClassIdentity) {
        if (relationClassIdentity==null)
            throw new NullPointerException("relationClassIdentity is null!");
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
        if (instanceIdentity==null)
            throw new NullPointerException("instanceIdentity is null!");
        if (alias==null)
            throw new NullPointerException("alias is null!");
        instanceAliasChanges.add(nameInstanceIdentityMap -> nameInstanceIdentityMap.put(alias, instanceIdentity));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass removeInstanceAlias(Name alias) {
        if (alias==null)
            throw new NullPointerException("alias is null!");
        instanceAliasChanges.add(nameInstanceIdentityMap -> nameInstanceIdentityMap.remove(alias));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass addMethod(Method.MethodIdentity methodIdentity, List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings, List<KeyValue<Method.ParamName, Message.Value>> defaultValues) {
        if (methodIdentity==null)
            throw new NullPointerException("methodIdentity is null!");
        methodChanges.add(methodIdentities -> methodIdentities.add(MethodClassImpl.create(methodIdentity, fieldMappings, defaultValues)));
        return this;
    }

    @Override
    public InstanceClass.ChangeInstanceClass removeMethod(Method.MethodIdentity methodIdentity) {
        methodChanges.add(methodIdentities -> {
            methodIdentities.stream()
                    .filter(methodClass -> methodClass.getMethodIdentity().equals(methodIdentity))
                    .forEach(methodIdentities::remove);
        });
        return this;
    }

    @Override
    public <D extends Data<InstanceClass.InstanceClassIdentity, StandardVersion>> D create(StandardVersion version) {
        if (!instanceAliasChanges.isEmpty()) {
            Map<Name,Instance.InstanceIdentity> newInstanceAliases = new HashMap<>();
            instanceAliases.forEach(nameInstanceIdentityEntry -> newInstanceAliases.put(nameInstanceIdentityEntry.getKey(), nameInstanceIdentityEntry.getValue()));
            instanceAliasChanges.forEach(mapConsumer -> mapConsumer.accept(newInstanceAliases));
            instanceAliases = newInstanceAliases.entrySet().stream();
        }
        InstanceClassDataImpl res = new InstanceClassDataImpl(
                name.orElse(instanceClassData.getName()),
                Util.updateIfChange(instanceClassData.getFields(), fieldChanges),
                Util.updateIfChange(instanceClassData.getRelations(), relationClassChanges),
                Util.updateIfChange(instanceClassData.getMethods(), methodChanges),
                instanceAliases.collect(Collectors.toList()),
                instanceClassData.getIdentity(),
                version);
        return (D) res;
    }
}
