package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.objectagon.core.rest2.model.Model;

/**
 * Created by christian on 2017-10-01.
 */
public class FieldDTO extends NameAndAliasDTO implements Model.Field {
    private String type;
    private String defaultValue;

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public String getDefaultValue() {return defaultValue;}
    @JsonSetter("default") public void setDefaultValue(String defaultValue) {this.defaultValue = defaultValue;}
}
