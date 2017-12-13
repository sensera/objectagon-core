package org.objectagon.core.rest2.dto;

import org.objectagon.core.rest2.model.Model;

/**
 * Created by christian on 2017-10-01.
 */
public class ValueDTO implements Model.Value {
    private String value;
    private String field;

    @Override public String getValue() {return value;}
    public void setValue(String value) {this.value = value;}
    @Override public String getField() {return field;}
}
