package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.objectagon.core.rest2.model.Model;

/**
 * Created by christian on 2017-10-01.
 */
public class ClassMethodParamDTO extends NameAndAliasDTO implements Model.ClassMethodParam {
    private String field;
    private String defaultValue;

    @Override public String getParam() {return getName();}
    public void setParam(String param) {this.setName(param);}
    @Override public String getField() {return field;}
    public void setField(String field) {this.field = field;}
    @Override public String getDefaultValue() {return defaultValue;}
    @JsonSetter("default") public void setDefaultValue(String defaultValue) {this.defaultValue = defaultValue;}

}
