package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.objectagon.core.rest2.model.Model;

import java.util.List;
import java.util.stream.Collectors;

public class InstanceDTO extends NameAndAliasDTO implements Model.Instance {
    private String className;
    private List<ValueDTO> values;
    private List<RelationDTO> relations;

    @JsonProperty("class") public String getClassName() {return className;}
    @JsonProperty("class") public void setClassName(String className) {this.className = className;}
    //public List<ValueDTO> getValues() {return values;}
    public void setValues(List<ValueDTO> values) {this.values = values;}
    //public List<RelationDTO> getRelations() {return relations;}
    public void setRelations(List<RelationDTO> relations) {this.relations = relations;}

    @Override
    public List<Model.Value> getValues() {
        return values.stream().map(valueDTO -> valueDTO).collect(Collectors.toList());
    }

    @Override
    public List<Model.Relation> getRelations() {
        return relations.stream().map(relationDTO -> relationDTO).collect(Collectors.toList());
    }
}
