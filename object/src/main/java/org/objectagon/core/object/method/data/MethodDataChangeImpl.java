package org.objectagon.core.object.method.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.method.InvokeParamImpl;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodDataChangeImpl implements Method.ChangeMethod {

    Method.MethodData methodData;
    Optional<String> newCode = Optional.empty();
    Optional<Method.MethodName> newName = Optional.empty();
    List<Consumer<List<Method.InvokeParam>>> invokeParamsChanges = new ArrayList<>();

    public MethodDataChangeImpl(Method.MethodData methodData) {
        this.methodData = methodData;
    }

    @Override
    public Method.ChangeMethod setCode(String code) {
        this.newCode = Optional.ofNullable(code);
        return this;
    }

    @Override
    public Method.ChangeMethod setName(Method.MethodName name) {
        this.newName = Optional.ofNullable(name);
        return this;
    }

    @Override
    public Method.ChangeMethod addInvokeParam(Method.ParamName paramName, Message.Field field) {
        invokeParamsChanges.add(invokeParams -> invokeParams.add(InvokeParamImpl.create(paramName, field)));
        return this;
    }

    @Override
    public Method.ChangeMethod addInvokeParam(Method.ParamName paramName, Message.Field field, Message.Value defaultValue) {
        invokeParamsChanges.add(invokeParams -> invokeParams.add(InvokeParamImpl.create(paramName, field, defaultValue)));
        return this;
    }

    @Override
    public Method.ChangeMethod removeInvokeParam(Method.ParamName paramName) {
        invokeParamsChanges.add(invokeParams -> {
            invokeParams.stream()
                    .filter(invokeParam -> invokeParam.getName().equals(paramName))
                    .findAny()
                    .ifPresent(invokeParams::remove);
        });
        return this;
    }

    @Override
    public <D extends Data<Method.MethodIdentity, StandardVersion>> D create(StandardVersion version) {
        List<Method.InvokeParam> invokeParams = methodData.getInvokeParams();
        if (!invokeParamsChanges.isEmpty()) {
            List<Method.InvokeParam> tmpInvokeParams = new ArrayList<>(invokeParams);
            invokeParamsChanges.stream().forEach(listConsumer -> listConsumer.accept(tmpInvokeParams));
            invokeParams = tmpInvokeParams;
        }
        return (D) new MethodDataImpl(
                methodData.getIdentity(),
                version,
                newCode.orElse(methodData.getCode()),
                newName.orElse(methodData.getName()),
                invokeParams
        );
    }
}

