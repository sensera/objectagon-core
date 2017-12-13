package org.objectagon.core.rest2.dto;

import org.objectagon.core.rest2.model.Model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-10-01.
 */
public class MethodDTO extends NameAndAliasDTO implements Model.Method {
    private String code;
    private List<MethodParamDTO> params;

    public List<Model.MethodParam> getParams() {
        return params.stream().map(methodParamDTO -> methodParamDTO).collect(Collectors.toList());
    }

    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public void setParams(List<MethodParamDTO> params) {this.params = params;}
}
