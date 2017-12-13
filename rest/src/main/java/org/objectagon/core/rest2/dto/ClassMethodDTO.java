package org.objectagon.core.rest2.dto;

import org.objectagon.core.rest2.model.Model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-10-01.
 */
public class ClassMethodDTO extends NameAndAliasDTO implements Model.ClassMethod {
    private String method;
    private String meta;
    private List<ClassMethodParamDTO> params;

    @Override
    public List<Model.ClassMethodParam> getParams() {
        return params.stream().map(classMethodParamDTO -> classMethodParamDTO).collect(Collectors.toList());
    }

    @Override public String getMethod() {return method;}
    @Override public String getMeta() {return meta;}

    public void setMethod(String method) {this.method = method;}
    public void setMeta(String meta) {this.meta = meta;}
    public void setParams(List<ClassMethodParamDTO> params) {this.params = params;}

}
