package org.objectagon.core.rest2.service;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-01-09.
 */
public interface RestServiceProtocol extends Protocol<RestServiceProtocol.Send, Protocol.Reply> {

    ProtocolName REST_SERVICE_PROTOCOL = new ProtocolNameImpl("REST_SERVICE_PROTOCOL");

    Message.Field METHOD_FIELD  = NamedField.name("methodField");
    Message.Field PATH_FIELD  = NamedField.name("pathField");
    Message.Field PARAMS_FIELD  = NamedField.values("paramsField");

    enum MessageName implements Message.MessageName, Task.TaskName {
        REST_REQUEST,
    }

    enum Method implements Name {
        GET,
        POST,
        PUT,
        DELETE
    }

    interface Send extends Protocol.Send {
        Task restRequest(Method method, Name path, List<KeyValue<ParamName, Message.Value>> params);
        Task restContent(long sequence, byte[] bytes);
        Task completed();
    }

    static Method getMethodFromString(String method) {
        if (method==null)
            return null;
        if (method.equalsIgnoreCase("GET"))
            return Method.GET;
        if (method.equalsIgnoreCase("POST"))
            return Method.POST;
        if (method.equalsIgnoreCase("PUT"))
            return Method.PUT;
        if (method.equalsIgnoreCase("DELETE"))
            return Method.DELETE;
        return null;
    }

    static Name getPathFromString(String path) {
        return StandardName.name(path);
    }

    static <E> List<KeyValue<ParamName, Message.Value>> getParams(List<E> params, Function<E,KeyValue<ParamName, Message.Value>> transform) {
        return params.stream()
                .map(transform)
                .collect(Collectors.toList());
    }

    static KeyValue<ParamName, Message.Value> createParamKeyValue(ParamName paramName, Message.Value value) {
        return new KeyValue<ParamName, Message.Value>() {
            @Override public ParamName getKey() {return paramName;}
            @Override public Message.Value getValue() {return value;}
        };
    }
}
