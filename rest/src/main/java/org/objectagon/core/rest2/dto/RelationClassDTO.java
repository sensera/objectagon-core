package org.objectagon.core.rest2.dto;

import org.objectagon.core.rest2.model.Model;

/**
 * Created by christian on 2017-10-01.
 */
public class RelationClassDTO extends NameAndAliasDTO implements Model.RelationClass {
    private String type;
    private String target;

    @Override public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    @Override public String getTarget() { return target;}
}
