package org.objectagon.core.rest2.dto;

import org.objectagon.core.rest2.model.Model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-10-01.
 */
public class MetaDTO extends NameAndAliasDTO implements Model.Meta {
    private List<MethodDTO> methods;

    //public List<MethodDTO> getMethods() {return methods;}
    public void setMethods(List<MethodDTO> methods) {this.methods = methods;}

    @Override
    public List<Model.Method> getMethods() {
        return methods.stream().map(methodDTO -> methodDTO).collect(Collectors.toList());
    }
}
