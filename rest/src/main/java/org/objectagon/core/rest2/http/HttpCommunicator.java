package org.objectagon.core.rest2.http;

import io.netty.buffer.ByteBuf;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-01-21.
 */
public interface HttpCommunicator {

    AsyncContent sendMessageWithAsyncContent(String method, String path, List<HttpParamValues> params, List<HttpParamValues> headers);

    interface Listener {
        void receive(HttpMessage httpMessage);
    }

    interface HttpMessage {
        String getMethod();
        String getPath();
        List<HttpParamValues> getParams();
        HttpMessgeContent getContent();
        void reply(String message);
        void reply(HttpMessgeContent message);
    }

    interface HttpMessgeContent {
        Long getSize();
        String asString();
        InputStream asInputStream();
    }

    interface HttpParamValues {
        String getParam();
        List<String> getValues();
        Message.Value asValue();
    }

    interface AsyncContent {
        void pushContent(String content);
        void pushContent(InputStream content);
        void pushContent(ByteBuf content);
        AsyncReply completed();
        void exceptionCaught(Throwable cause);
    }

    static HttpParamValues createHttpParamValuesFromEntry(Map.Entry<String,String> entry) {
        return createHttpParamValuesFromEntry(entry.getKey(), entry.getValue());
    }

    static HttpParamValues createHttpParamValuesFromEntry(String param, String value) {
        return createHttpParamValues(param, Collections.singletonList(value));
    }

    static HttpParamValues createHttpParamValues(String param, List<String> values) { return new HttpParamValues() {
        @Override public String getParam() {return param;}
        @Override public List<String> getValues() {return values;}

        @Override
        public Message.Value asValue() {
            if (values.isEmpty())
                return MessageValue.empty(NamedField.text(param));
            if (values.size() == 1)
                return MessageValue.text(NamedField.text(param), values.get(0));
            final List<Message.Value> listedValues = values.stream().map(MessageValue::text).collect(Collectors.toList());
            return MessageValue.values(NamedField.text(param), listedValues);
        }
    };}

    static Function<String,HttpParamValues> createHttpParamValueFunction(Map<String,List<String>> paramValues) {
        return name -> createHttpParamValues(name, paramValues.get(name));
    }

    static Function<String,HttpParamValues> createHttpParamValueFunction(Map.Entry<String,String> paramValues) {
        return name -> createHttpParamValuesFromEntry(name, paramValues.getValue());
    }

    interface AsyncReply {
        void receiveReply(Consumer<AsyncReplyContent> consumeReplyContent, Consumer<String> failed);
    }

    interface AsyncReplyContent {
        String getContent();
        Optional<String> token();
    }

}
