package org.objectagon.core.rest2.http;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.MessageValueMessage;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.msg.protocol.StandardProtocol;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.utils.JsonBuilder;
import org.objectagon.core.rest2.utils.JsonBuilderImpl;
import org.objectagon.core.rest2.utils.ReadStream;
import org.objectagon.core.task.Task;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * Created by christian on 2017-03-05.
 */
class SimpleLocalMessageSession implements HttpCommunicator.AsyncContent, Task.SuccessAction, Task.FailedAction, HttpCommunicator.AsyncReply {

    private final RestServiceProtocol.SimplifiedSend send;
    private Consumer<HttpCommunicator.AsyncReplyContent> consumeReplyContent;
    private Consumer<String> failed;
    private ByteArrayOutputStream out;
    private BufferedWriter writer;
    private String method;
    private String path;
    private List<HttpCommunicator.HttpParamValues> params;
    private JsonBuilder jsonBuilder = new JsonBuilderImpl();

    private HttpCommunicator.AsyncReplyContent asynSuccess = null;
    private String asyncFailed = null;

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
    public synchronized void receiveReply(Consumer<HttpCommunicator.AsyncReplyContent> consumeReplyContent, Consumer<String> failed) {
        this.consumeReplyContent = consumeReplyContent;
        this.failed = failed;

        if (asynSuccess != null) {
            consumeReplyContent.accept(asynSuccess);
            asynSuccess = null;
            send.terminate();
        } else if (asyncFailed != null) {
            failed.accept(asyncFailed);
            asyncFailed = null;
            send.terminate();
        }
    }

    @Override
    public synchronized void failed(StandardProtocol.ErrorClass errorClass, StandardProtocol.ErrorKind errorKind, Iterable<Message.Value> values) {
        final JsonBuilder.Builder builder = jsonBuilder.builder();
        builder.setChildValue(StandardName.name("errorClass"), errorClass.name());
        builder.setChildValue(StandardName.name("errorKind"), errorKind.name());
        StreamSupport.stream(values.spliterator(), false)
                .forEach(childValue -> addJsonChild(builder, StandardName.name("value"), childValue));
        if (failed == null) {
            asyncFailed = builder.build().toString();
            return;
        }
        failed.accept(builder.build().toString());
        send.terminate();
    }

    @Override
    public synchronized void success(Message.MessageName messageName, Iterable<Message.Value> values) throws UserException {
        final JsonBuilder.Builder builder = jsonBuilder.builder();
        //final Map<? extends Name, ? extends Message.Value> map = MessageValueFieldUtil.create(values).getValueByField(RestServiceProtocol.REST_CONTENT_RESPONSE).asMap();
        createJson(builder, values);
        System.out.println("SimpleLocalMessageSession.success **********************************");
        System.out.println(builder.build().toString());
        System.out.println("SimpleLocalMessageSession.success **********************************");
        final HttpCommunicator.AsyncReplyContent asyncReplyContent = new HttpCommunicator.AsyncReplyContent() {
            @Override public String getContent() {return builder.build().toString();}

            @Override public Optional<String> token() {
                return Optional.empty();
            }
        };
        if (consumeReplyContent==null) {
            asynSuccess = asyncReplyContent;
            return;
        }
        consumeReplyContent.accept(asyncReplyContent);
        send.terminate();
    }

    private void createJson(JsonBuilder.Builder builder, Map<? extends Name, ? extends Message.Value> map) {
        map.entrySet().stream().forEach(entry -> addJsonChild(builder, entry.getKey(), entry.getValue()));
    }

    private void createJson(JsonBuilder.Builder builder, Iterable<Message.Value> values) {
        values.forEach(value -> addJsonChild(builder, value.getField().getName(), value));
    }

    private void addJsonChild(JsonBuilder.Item item, Name name, Message.Value value) {
        final JsonBuilder.Item childItem = item.addChild(name);
        value.writeTo(new Message.Writer() {
            @Override
            public void write(Message.Field field, String text) {
                childItem.set(text);
            }

            @Override
            public void write(Message.Field field, Long number) {
                childItem.set(number);
            }

            @Override
            public void write(Message.Field field, Map<? extends Name, ? extends Message.Value> map) {
                map.entrySet().stream().forEach(entry -> addJsonChild(childItem, entry.getKey(), entry.getValue()));
            }

            @Override public <V> void writeAny(Message.Field field, V value) {
                if (value==null)
                    write(field, "null");
                else
                    write(field, value.toString());
            }

            @Override
            public void write(Message.Field field, MessageValueMessage message) {
                StreamSupport.stream(message.getValues().spliterator(), false)
                        .forEach(childValue -> addJsonChild(childItem, field.getName(), childValue));
            }

            @Override
            public void write(Message.Field field, Message.MessageName messageName) {
                childItem.setChildValue(field.getName(), Name.getNameAsString(messageName));
            }

            @Override
            public void write(Message.Field field, Address address) {
                childItem.setChildValue(field.getName(), address.toString());
            }

            @Override
            public void write(Message.Field field, Name name) {
                childItem.setChildValue(field.getName(), Name.getNameAsString(name));
            }

            @Override
            public void write(Message.Field field, Message.Values values) {
                StreamSupport.stream(values.values().spliterator(), false)
                        .forEach(childValue -> addJsonChild(childItem, field.getName(), childValue));
            }
        });
    }
}