package org.objectagon.core.rest2;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.KeyValue;

import java.util.List;

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
    }
}
