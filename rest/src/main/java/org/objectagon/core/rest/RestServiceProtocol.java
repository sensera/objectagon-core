package org.objectagon.core.rest;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.task.Task;

import java.util.List;

/**
 * Created by christian on 2016-03-07.
 */
public interface RestServiceProtocol extends Protocol<RestServiceProtocol.Send, Protocol.Reply> {

    ProtocolName REST_SERVICE_PROTOCOL = new ProtocolNameImpl("REST_SERVICE_PROTOCOL");

    Message.Field METHOD_TYPE = NamedField.name("methodType");
    Message.Field URL = NamedField.text("url");
    Message.Field HEADERS = NamedField.values("headers");

    Message.Field CONTENT = NamedField.blob("content");
    Message.Field POSITION = NamedField.number("position");
    Message.Field LENGTH = NamedField.number("length");

    SendContent createSendContent(CreateSendParam createSend);

    enum MethodType implements Name {
        GET,
        POST,
        PUT,
        DELETE
    }

    enum MessageName implements Message.MessageName, Task.TaskName {
        HTTP_SIMPLE_CALL,
        HTTP_CONTENT_CALL,
    }


    interface Send extends Protocol.Send {
        Task httpSimpleCall(MethodType methodType, String url, List<Message.Value> headers);
    }

    interface SendContent extends Protocol.Send {
        Task httpCallStart(MethodType methodType, String url, List<Message.Value> headers, byte[] content, Long position, Long length);
        Task httpCallStart(byte[] content, Long position, Long length);
    }

 }
