package org.objectagon.core.rest2.dto;

import org.objectagon.core.rest2.model.Model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-10-01.
 */
public class ClassDTO extends NameAndAliasDTO implements Model.InstanceClass {
    private List<FieldDTO> fields;
    private List<RelationClassDTO> relationClasses;
    private List<ClassMethodDTO> methods;

    public void setFields(List<FieldDTO> fields) {this.fields = fields;}
    public void setRelationClasses(List<RelationClassDTO> relationClasses) {this.relationClasses = relationClasses;}
    public void setMethods(List<ClassMethodDTO> methods) {this.methods = methods;}

    @Override
    public List<Model.ClassMethod> getMethods() {
        return methods.stream().map(classMethodDTO -> classMethodDTO).collect(Collectors.toList());
    }

    @Override
    public List<Model.Field> getFields() {
        return fields.stream().map(fieldDTO -> fieldDTO).collect(Collectors.toList());
    }

    @Override
    public List<Model.RelationClass> getRelationClasses() {
        return relationClasses.stream().map(relationClassDTO -> relationClassDTO).collect(Collectors.toList());
    }

}
