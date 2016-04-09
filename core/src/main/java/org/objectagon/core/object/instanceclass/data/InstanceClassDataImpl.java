package org.objectagon.core.object.instanceclass.data;

import org.objectagon.core.object.Field;
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
        return new InstanceClassDataImpl(InstanceClassNameImpl.create(""), Stream.empty(), Stream.empty(), identity, version);
    }

    List<Field.FieldIdentity> fields;
    List<RelationClass.RelationClassIdentity> relations;
    private InstanceClass.InstanceClassName name;

    InstanceClassDataImpl(InstanceClass.InstanceClassName name, Stream<Field.FieldIdentity> fields, Stream<RelationClass.RelationClassIdentity> relations, InstanceClass.InstanceClassIdentity identity, StandardVersion version) {
        super(identity, version);
        this.name = name;
        this.fields = fields.collect(Collectors.toList());
        this.relations = relations.collect(Collectors.toList());
    }

    @Override public Stream<Field.FieldIdentity> getFields() {return fields.stream();}
    @Override public Stream<RelationClass.RelationClassIdentity> getRelations() {return relations.stream();}
    @Override public InstanceClass.InstanceClassName getName() {return null;}

    @Override
    public <C extends Change<InstanceClass.InstanceClassIdentity, StandardVersion>> C change() {
        return (C) new ChangeInstanceClassImpl(this);
    }

}
