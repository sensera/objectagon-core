package org.objectagon.core.rest2.http;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.utils.ReadStream;
import org.objectagon.core.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by christian on 2017-03-05.
 */
class LocalMessageSession implements HttpCommunicator.AsyncContent, Task.SuccessAction, Task.FailedAction, HttpCommunicator.AsyncReply {

    private final RestServiceProtocol.Send send;
    private Consumer<String> consumeReplyContent;
    private Consumer<Exception> failed;
    private long contentSequence = 0;

    public LocalMessageSession(RestServiceProtocol.Send send) {
        this.send = send;
    }

    LocalMessageSession sendRestRequest(String method, String path, List<HttpCommunicator.HttpParamValues> params) {
        send.restRequest(RestServiceProtocol.getMethodFromString(method),
                RestServiceProtocol.getPathFromString(path),
                RestServiceProtocol.getParams(params, HttpService.getHttpParamValuesKeyValueFunction()))
                .addFailedAction(this)
                .addSuccessAction(this)
                .start();
        return this;
    }

    private synchronized long nextContentSequenceNumber() {
        return contentSequence++;
    }

    @Override
    public void pushContent(String content) {
        send.restContent(nextContentSequenceNumber(), content.getBytes()).start();
    }

    @Override
    public void pushContent(InputStream content) {
        try {
            send.restContent(nextContentSequenceNumber(), ReadStream.readStream(content)).start();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO complete failed
            //failed();
        }
    }

    @Override
    public void pushContent(ByteBuf content) {
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        send.restContent(nextContentSequenceNumber(), bytes).start();
    }

    @Override
    public HttpCommunicator.AsyncReply completed() {
        send.completed().start();
        return this;
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        send.exceptionCaught(cause).start();
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
