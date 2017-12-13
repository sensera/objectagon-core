package org.objectagon.core.rest2.model;

import java.util.List;

/**
 * Created by christian on 2017-10-01.
 */
public interface Model {

    List<Meta> getMetas();
    List<InstanceClass> getClasses();
    List<Instance> getInstances();

    interface Meta extends NameAndAlias {
        List<Method> getMethods();
    }

    interface Method extends NameAndAlias {
        String getCode();
        List<MethodParam> getParams();
    }

    interface MethodParam extends NameAndAlias {
        String getField();
        String getDefaultValue();
    }

    interface InstanceClass extends NameAndAlias {
        List<Field> getFields();
        List<RelationClass> getRelationClasses();
        List<ClassMethod> getMethods();
    }

    interface Field extends NameAndAlias {
        String getType();
        String getDefaultValue();
    }

    interface RelationClass extends NameAndAlias {
        String getType();
        String getTarget();
    }

    interface ClassMethod extends NameAndAlias {
        String getMethod();
        String getMeta();
        List<ClassMethodParam> getParams();
    }

    interface ClassMethodParam extends NameAndAlias {
        String getParam();
        String getField();
        String getDefaultValue();
    }

    interface Instance extends NameAndAlias {
        String getClassName();
        List<Value> getValues();
        List<Relation> getRelations();
    }

    interface Value  {
        String getField();
        String getValue();
    }

    interface Relation  {
        String getRelationClass();
        String getTargetInstance();
    }

    interface NameAndAlias {
        String getName();
        String getAlias();
    }

}
