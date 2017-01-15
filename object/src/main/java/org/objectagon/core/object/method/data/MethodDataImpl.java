package org.objectagon.core.object.method.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Method;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodDataImpl extends AbstractData<Method.MethodIdentity, StandardVersion> implements Method.MethodData {

    public static MethodDataImpl create(Method.MethodIdentity identity, StandardVersion version) {
        return new MethodDataImpl(identity, version, null, null, Collections.EMPTY_LIST);
    }

    private String code;
    private Method.MethodName name;
    private List<Method.InvokeParam> invokeParams;

    MethodDataImpl(Method.MethodIdentity identity, StandardVersion version, String code, Method.MethodName name, List<Method.InvokeParam> invokeParams) {
        super(identity, version);
        this.code = code;
        this.name = name;
        this.invokeParams = invokeParams;
    }

    @Override
    public <C extends Change<Method.MethodIdentity, StandardVersion>> C change() {
        return (C) new MethodDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        final List<Message.Value> invokeParamsList = invokeParams.stream().map(invokeParam -> MessageValue.name(invokeParam.getName())).collect(Collectors.toList());
        return Arrays.asList(
                MessageValue.text(Method.CODE, code),
                MessageValue.name(Method.METHOD_NAME, name),
                MessageValue.values(Method.PARAMS, invokeParamsList)
        );
    }

    @Override public String getCode() {return code;}

    @Override
    public List<Method.InvokeParam> getInvokeParams() {
        return invokeParams;
    }

    @Override public Method.MethodName getName() {return name;}
}
