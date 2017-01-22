package org.objectagon.core.rest2.http;

import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by christian on 2017-01-21.
 */
public interface HttpCommunicator {

    AsyncContent sendMessageWithAsyncContent(String method, String path, List<HttpParamValues> params);

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
    }

    interface AsyncContent {
        void pushContent(String content);
        void pushContent(InputStream content);
        void pushContent(ByteBuf content);
        AsyncReply completed();
    }

    static HttpParamValues createHttpParamValues(String param, List<String> values) { return new HttpParamValues() {
        @Override public String getParam() {return param;}
        @Override public List<String> getValues() {return values;}
    };}

    static Function<String,HttpParamValues> createHttpParamValueFunction(Map<String,List<String>> paramValues) {
        return name -> createHttpParamValues(name, paramValues.get(name));
    }

    interface AsyncReply {
        void receiveReply(Consumer<String> consumeReplyContent, Consumer<Exception> failed);
    }

}
