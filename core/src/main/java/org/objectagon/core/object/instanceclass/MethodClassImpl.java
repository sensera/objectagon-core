package org.objectagon.core.object.instanceclass;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.Method;
import org.objectagon.core.utils.KeyValue;

import java.util.List;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodClassImpl implements InstanceClass.MethodClass {

    public static InstanceClass.MethodClass create(Method.MethodIdentity methodIdentity, List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings, List<KeyValue<Method.ParamName, Message.Value>> defaultValues) {
        return new MethodClassImpl(methodIdentity, fieldMappings, defaultValues);
    }

    private Method.MethodIdentity methodIdentity;
    private List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings;
    private List<KeyValue<Method.ParamName, Message.Value>> defaultValues;

    public MethodClassImpl(Method.MethodIdentity methodIdentity, List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings, List<KeyValue<Method.ParamName, Message.Value>> defaultValues) {
        this.methodIdentity = methodIdentity;
        this.fieldMappings = fieldMappings;
        this.defaultValues = defaultValues;
    }

    @Override public Method.MethodIdentity getMethodIdentity() {return methodIdentity;}
    @Override public List<KeyValue<Method.ParamName, Field.FieldIdentity>> getFieldMappings() {return fieldMappings;}
    @Override public List<KeyValue<Method.ParamName, Message.Value>> getDefaultValues() {return defaultValues;}
}
