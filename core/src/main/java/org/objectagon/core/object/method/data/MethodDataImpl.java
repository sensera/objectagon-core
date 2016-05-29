package org.objectagon.core.object.method.data;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Method;
import org.objectagon.core.storage.data.AbstractData;
import org.objectagon.core.storage.standard.StandardVersion;

import java.util.Arrays;

/**
 * Created by christian on 2016-05-29.
 */
public class MethodDataImpl extends AbstractData<Method.MethodIdentity, StandardVersion> implements Method.MethodData {

    public static MethodDataImpl create(Method.MethodIdentity identity, StandardVersion version) {
        return new MethodDataImpl(identity, version, null, null);
    }

    private String code;
    private Method.MethodName name;

    MethodDataImpl(Method.MethodIdentity identity, StandardVersion version, String code, Method.MethodName name) {
        super(identity, version);
        this.code = code;
        this.name = name;
    }

    @Override
    public <C extends Change<Method.MethodIdentity, StandardVersion>> C change() {
        return (C) new MethodDataChangeImpl(this);
    }

    @Override
    public Iterable<Message.Value> values() {
        return Arrays.asList(
                MessageValue.text(Method.CODE, code),
                MessageValue.name(Method.METHOD_NAME, name)
        );
    }

    @Override public String getCode() {return code;}
    @Override public Method.MethodName getName() {return name;}
}
