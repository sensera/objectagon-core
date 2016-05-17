package org.objectagon.core.object.field;

public class FieldNameImplBuilder {
    private String name;

    public FieldNameImplBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public FieldNameImpl createFieldNameImpl() {
        return FieldNameImpl.create(name);
    }
}