package org.objectagon.core.rest2.http;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.utils.ReadStream;
import org.objectagon.core.task.Task;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by christian on 2017-03-05.
 */
class SimpleLocalMessageSession implements HttpCommunicator.AsyncContent, Task.SuccessAction, Task.FailedAction, HttpCommunicator.AsyncReply {

    private final RestServiceProtocol.SimplifiedSend send;
    private Consumer<String> consumeReplyContent;
    private Consumer<Exception> failed;
    private ByteArrayOutputStream out;
    private BufferedWriter writer;
    private String method;
    private String path;
    private List<HttpCommunicator.HttpParamValues> params;

    public SimpleLocalMessageSession(RestServiceProtocol.SimplifiedSend send) {
        this.send = send;
        out = new ByteArrayOutputStream();
        writer = new BufferedWriter(new OutputStreamWriter(out));
    }

    SimpleLocalMessageSession sendRestRequest(String method, String path, List<HttpCommunicator.HttpParamValues> params) {
        this.method = method;
        this.path = path;
        this.params = params;
        return this;
    }

    @Override
    public void pushContent(String content) {
        try {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pushContent(InputStream content) {
        try {
            writer.write(new String(ReadStream.readStream(content)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pushContent(ByteBuf content) {
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        try {
            writer.write(new String(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpCommunicator.AsyncReply completed() {
        getContentFromBufferedStream();
        send.restRequest(RestServiceProtocol.getMethodFromString(method),
                RestServiceProtocol.getPathFromString(path),
                getContentFromBufferedStream(),
                RestServiceProtocol.getParams(params, HttpService.getHttpParamValuesKeyValueFunction()))
                .addSuccessAction(this)
                .addFailedAction(this)
                .start();
        return this;
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        cause.printStackTrace();
    }

    private String getContentFromBufferedStream() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (out.toByteArray() == null)
            return "";
        return new String(out.toByteArray());
    }

    @Override
    public void receiveReply(Consumer<String> consumeReplyContent, Consumer<Exception> failed) {
        this.consumeReplyContent = consumeReplyContent;
        this.failed = failed;
    }

    @Override
    public void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        failed.accept(new Exception(errorClass.name() + " " + errorKind.name()));
        send.terminate();
    }

    @Override
    public void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
        consumeReplyContent.accept(messageName.toString());
        send.terminate();
    }
}
