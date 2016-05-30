package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-29.
 */
public interface MethodProtocol extends Protocol<MethodProtocol.Send, Protocol.Reply> {

    ProtocolName METHOD_PROTOCOL = new ProtocolNameImpl("METHOD_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        GET_CODE,
        SET_CODE,
        INVOKE,
        ADD_PARAM,
        REMOVE_PARAM
    }

    interface Send extends Protocol.Send {
        Task getCode();
        Task setCode(String code);
        Task invoke(MessageValue... params);
        Task addParam(Method.ParamName paramName, Message.Field field, Message.Value defaultValue);
        Task removeParam(Method.ParamName paramName);
    }
}
