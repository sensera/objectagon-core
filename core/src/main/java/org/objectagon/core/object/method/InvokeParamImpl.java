package org.objectagon.core.object.method;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Method;

import java.util.Optional;

/**
 * Created by christian on 2016-05-29.
 */
public class InvokeParamImpl implements Method.InvokeParam {

    private final Method.ParamName name;
    private final Message.Field field;
    private final Message.Value defaultValue;

    private InvokeParamImpl(Method.ParamName name, Message.Field field, Message.Value defaultValue) {
        this.name = name;
        this.field = field;
        this.defaultValue = defaultValue;
    }

    @Override
    public Method.ParamName getName() {
        return name;
    }

    @Override
    public Optional<Message.Value> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public Message.Field getField() {
        return field;
    }

    public static Method.InvokeParam create(Method.ParamName name, Message.Field field, Message.Value defaultValue) {
        return new InvokeParamImpl(name, field, defaultValue);
    }

    public static Method.InvokeParam create(Method.ParamName name, Message.Field field) {
        return new InvokeParamImpl(name, field, null);
    }
}
