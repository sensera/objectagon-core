package org.objectagon.core.object.instanceclass;

import org.objectagon.core.msg.Converter;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.Relation;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassDataImpl extends AbstractData<InstanceClass.InstanceClassIdentity, StandardVersion> implements InstanceClass.InstanceClassData {

    private static final Field.FieldType STANDARD_FIELD_TYPE = null;

    Fields fields = new Fields();
    Relations relations = new Relations();

    public InstanceClassDataImpl(Stream<InstanceClass.InstanceClassDataField> fields, Stream<InstanceClass.InstanceClassDataRelation> relations, InstanceClass.InstanceClassIdentity identity, StandardVersion version) {
        super(identity, version);
        fields.forEach(this.fields::add);
        relations.forEach(this.relations::add);
    }

    @Override
    public void convert(Converter.FromData<Data<InstanceClass.InstanceClassIdentity, StandardVersion>> fromData) {

    }

    @Override
    public Stream<Field.FieldName> getFieldNames() {
        return fields.getFieldNames();
    }

    @Override
    public Stream<Relation.RelationName> getRelationNames() {
        return relations.getRelationNames();
    }

    @Override
    public InstanceClass.InstanceClassDataField getField(Field.FieldName fieldName) {
        return fields.getField(fieldName);
    }

    @Override
    public InstanceClass.InstanceClassDataRelation getRelation(Relation.RelationName relationName) {
        return relations.getRelation(relationName);
    }

    @Override
    public <C extends Change<InstanceClass.InstanceClassIdentity, StandardVersion>> C change() {
        return (C) new ChangeInstanceClassImpl(
                fields.stream(),
                relations.stream(),
                getVersion(),
                getIdentity()
        );
    }

    private static class InstanceClassDataFieldImpl implements InstanceClass.InstanceClassDataField {
        private Field.FieldName name;
        private Field.FieldType type;

        @Override public Field.FieldName getName() {return name;}
        @Override public Field.FieldType getType() {return type;}
        public void setName(Field.FieldName name) {this.name = name;}
        public void setType(Field.FieldType type) {this.type = type;}

        public InstanceClassDataFieldImpl(Field.FieldName name, Field.FieldType type) {
            this.name = name;
            this.type = type;
        }

        public InstanceClassDataFieldImpl(InstanceClass.InstanceClassDataField field) {
            this(field.getName(), field.getType());
        }

        InstanceClassDataFieldImpl copy() { return new InstanceClassDataFieldImpl(name, type);}
    }

    private static class InstanceClassDataRelationImpl implements InstanceClass.InstanceClassDataRelation {
        private Relation.RelationName name;
        private Relation.RelationType type;
        private Relation.RelationDirection direction;

        @Override public Relation.RelationName getName() {return name;}
        @Override public Relation.RelationType getType() {return type;}
        @Override public Relation.RelationDirection getDirection() {return direction;}
        public void setName(Relation.RelationName name) {this.name = name;}
        public void setType(Relation.RelationType type) {this.type = type;}
        public void setDirection(Relation.RelationDirection direction) {this.direction = direction;}

        public InstanceClassDataRelationImpl(Relation.RelationName name, Relation.RelationType type, Relation.RelationDirection direction) {
            this.name = name;
            this.type = type;
            this.direction = direction;
        }

        public InstanceClassDataRelationImpl(InstanceClass.InstanceClassDataRelation relation) {
            this(relation.getName(), relation.getType(), relation.getDirection());
        }

        InstanceClassDataRelationImpl copy() { return new InstanceClassDataRelationImpl(name, type, direction);}
    }

    private static class ChangeInstanceClassImpl implements InstanceClass.ChangeInstanceClass, InstanceClass.ChangeField, InstanceClass.ChangeRelation {

        Map<Field.FieldName, InstanceClassDataFieldImpl> fields = new HashMap<>();
        Map<Relation.RelationName, InstanceClassDataRelationImpl> relations = new HashMap<>();
        InstanceClass.InstanceClassIdentity instanceClassIdentity;
        StandardVersion version;

        Optional<InstanceClassDataFieldImpl> currentField;
        Optional<InstanceClassDataRelationImpl> currentRelation;

        Optional<InstanceClassDataFieldImpl> getInstanceClassDataField(Field.FieldName fieldName) { return Optional.ofNullable(fields.get(fieldName)); }
        Optional<InstanceClassDataRelationImpl> getInstanceClassDataRelation(Relation.RelationName relationName) { return Optional.ofNullable(relations.get(relationName)); }

        public ChangeInstanceClassImpl(Stream<InstanceClassDataFieldImpl> fields, Stream<InstanceClassDataRelationImpl> relations, StandardVersion version, InstanceClass.InstanceClassIdentity instanceClassIdentity) {
            this.instanceClassIdentity = instanceClassIdentity;
            this.fields = fields.map(InstanceClassDataFieldImpl::copy)
                    .collect(Collectors.toMap(InstanceClassDataFieldImpl::getName, Function.<InstanceClassDataFieldImpl>identity()));
            this.relations = relations.map(InstanceClassDataRelationImpl::copy)
                    .collect(Collectors.toMap(InstanceClassDataRelationImpl::getName, Function.<InstanceClassDataRelationImpl>identity()));
            this.version = version;
        }

        @Override
        public InstanceClass.ChangeField setType(Field.FieldType type) {
            currentField.ifPresent(instanceClassDataField -> instanceClassDataField.setType(type));
            return this;
        }

        @Override
        public InstanceClass.ChangeRelation setType(Relation.RelationType type) {
            currentRelation.ifPresent(instanceClassDataRelation -> instanceClassDataRelation.setType(type));
            return this;
        }

        @Override
        public InstanceClass.ChangeRelation setDirection(Relation.RelationDirection direction) {
            currentRelation.ifPresent(instanceClassDataRelation -> instanceClassDataRelation.setDirection(direction));
            return this;
        }

        @Override
        public InstanceClass.ChangeField setField(Field.FieldName fieldName) {
            currentField = getInstanceClassDataField(fieldName);
            if (!currentField.isPresent())
                currentField = Optional.of(new InstanceClassDataFieldImpl(fieldName, STANDARD_FIELD_TYPE));
            return this;
        }

        @Override
        public InstanceClass.ChangeRelation setRelation(Relation.RelationName relationName) {
            currentRelation = getInstanceClassDataRelation(relationName);
            if (!currentRelation.isPresent())
                currentRelation = Optional.of(new InstanceClassDataRelationImpl(relationName, Relation.RelationType.ASSOCIATION, Relation.RelationDirection.OWNER));
            return this;
        }


        @Override
        public <D extends Data<InstanceClass.InstanceClassIdentity, StandardVersion>> D create(StandardVersion version) {
            InstanceClassDataImpl res = new InstanceClassDataImpl(
                    fields.values().stream().map(instanceClassDataField -> (InstanceClass.InstanceClassDataField) instanceClassDataField),
                    relations.values().stream().map(instanceClassDataRelation -> (InstanceClass.InstanceClassDataRelation) instanceClassDataRelation),
                    instanceClassIdentity,
                    version);
            return (D) res;
        }
    }

    private static class Fields {
        Map<Field.FieldName, InstanceClassDataFieldImpl> fields = new HashMap<>();

        public void add(InstanceClass.InstanceClassDataField instanceClassDataField) {
            this.fields.put(instanceClassDataField.getName(), new InstanceClassDataFieldImpl(instanceClassDataField));
        }

        public Stream<Field.FieldName> getFieldNames() {
            return fields.keySet().stream();
        }

        public InstanceClass.InstanceClassDataField getField(Field.FieldName fieldName) {
            return fields.get(fieldName);
        }

        public Stream<InstanceClassDataFieldImpl> stream() {
            return fields.values().stream();
        }
    }

    private static class Relations {
        Map<Relation.RelationName, InstanceClassDataRelationImpl> relations = new HashMap<>();

        public void add(InstanceClass.InstanceClassDataRelation instanceClassDataRelation) {
            this.relations.put(instanceClassDataRelation.getName(), new InstanceClassDataRelationImpl(instanceClassDataRelation));
        }

        public Stream<Relation.RelationName> getRelationNames() {
            return relations.keySet().stream();
        }

        public InstanceClass.InstanceClassDataRelation getRelation(Relation.RelationName relationName) {
            return relations.get(relationName);
        }

        public Stream<InstanceClassDataRelationImpl> stream() {
            return relations.values().stream();
        }
    }


}
