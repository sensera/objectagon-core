package org.objectagon.core.object.method.data;

import org.objectagon.core.object.Method;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Optional;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodDataChangeImpl implements Method.ChangeMethod {

    Method.MethodData methodData;
    Optional<String> newCode = Optional.empty();
    Optional<Method.MethodName> newName = Optional.empty();

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
    public <D extends Data<Method.MethodIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D) new MethodDataImpl(
                methodData.getIdentity(),
                version,
                newCode.orElse(methodData.getCode()),
                newName.orElse(methodData.getName())
        );
    }
}

