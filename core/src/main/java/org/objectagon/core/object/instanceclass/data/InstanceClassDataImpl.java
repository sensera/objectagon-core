package org.objectagon.core.object.instanceclass.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.instanceclass.InstanceClassNameImpl;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.object.RelationClass;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassDataImpl extends AbstractData<InstanceClass.InstanceClassIdentity, StandardVersion> implements InstanceClass.InstanceClassData {

    public static InstanceClassDataImpl create(InstanceClass.InstanceClassIdentity identity, StandardVersion version) {
        return new InstanceClassDataImpl(InstanceClassNameImpl.create(""), Stream.empty(), Stream.empty(), Stream.empty(), identity, version);
    }

    private List<Field.FieldIdentity> fields;
    private List<RelationClass.RelationClassIdentity> relations;
    private InstanceClass.InstanceClassName name;
    private Map<Name,Instance.InstanceIdentity> instanceAliases = new HashMap<>();

    InstanceClassDataImpl(InstanceClass.InstanceClassName name, Stream<Field.FieldIdentity> fields, Stream<RelationClass.RelationClassIdentity> relations, Stream<Map.Entry<Name,Instance.InstanceIdentity>> instanceAliases, InstanceClass.InstanceClassIdentity identity, StandardVersion version) {
        super(identity, version);
        this.name = name;
        this.fields = fields.collect(Collectors.toList());
        this.relations = relations.collect(Collectors.toList());
        instanceAliases.forEach(nameInstanceIdentityEntry -> this.instanceAliases.put(nameInstanceIdentityEntry.getKey(), nameInstanceIdentityEntry.getValue()));
    }

    @Override public Stream<Field.FieldIdentity> getFields() {return fields.stream();}
    @Override public Stream<RelationClass.RelationClassIdentity> getRelations() {return relations.stream();}
    @Override public InstanceClass.InstanceClassName getName() {return name;}

    @Override
    public Optional<Instance.InstanceIdentity> getInstanceByAliasName(Name alias) {
        return Optional.ofNullable(instanceAliases.get(alias));
    }

    @Override
    public <C extends Change<InstanceClass.InstanceClassIdentity, StandardVersion>> C change() {
        return (C) new ChangeInstanceClassImpl(this, instanceAliases.entrySet().stream());
    }

    @Override
    public Iterable<Message.Value> values() {
        //TODO Fix
        return Arrays.asList(
                MessageValue.name(InstanceClass.INSTANCE_CLASS_NAME, name),
                MessageValue.number(InstanceClass.FIELDS, (long) fields.size()),
                MessageValue.number(InstanceClass.RELATIONS, (long) fields.size())
        );
    }

    @Override
    public String toString() {
        return "InstanceClassDataImpl{" +
                "fields=" + fields +
                ", relations=" + relations +
                ", name=" + name +
                '}';
    }
}
