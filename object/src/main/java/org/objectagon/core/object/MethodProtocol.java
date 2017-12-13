package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

import java.util.List;

/**
 * Created by christian on 2016-05-29.
 */
public interface MethodProtocol extends Protocol<MethodProtocol.Send, Protocol.Reply> {

    ProtocolName METHOD_PROTOCOL = new ProtocolNameImpl("METHOD_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        GET_NAME,
        SET_NAME,
        GET_CODE,
        SET_CODE,
        INVOKE,
        ADD_PARAM,
        REMOVE_PARAM
    }

    interface Send extends Protocol.Send {
        Task getName();
        Task setName(Method.MethodName name);
        Task getCode();
        Task setCode(String code);
        Task invoke(List<KeyValue<Method.ParamName, Message.Value>> paramNameValueList);
        Task addParam(Method.ParamName paramName, Message.Field field, Message.Value defaultValue);
        Task removeParam(Method.ParamName paramName);
    }
}
