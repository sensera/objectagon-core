package org.objectagon.core.rest2.dto;

import org.objectagon.core.rest2.model.Model;

/**
 * Created by christian on 2017-10-01.
 */
public class RelationDTO implements Model.Relation{
    private String relationClass;
    private String targetInstance;

    @Override public String getRelationClass() {return relationClass;}
    public void setRelationClass(String relationClass) {this.relationClass = relationClass;}
    @Override public String getTargetInstance() { return targetInstance; }
    public void setTargetInstance(String targetInstance) {this.targetInstance = targetInstance;}
}
