package org.objectagon.core.rest2.http;

import io.netty.buffer.ByteBuf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectagon.core.rest2.utils.WriteStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.objectagon.core.rest2.utils.ReadStream.readStream;

/**
 * Created by christian on 2017-01-22.
 */
public class HttpServerImplTest {

    HttpServerImpl httpServerImpl;
    TestHttpCommunicator httpCommunicator;

    @Before
    public void setUp() throws Exception {
        httpCommunicator = new TestHttpCommunicator();
        httpServerImpl = new HttpServerImpl(httpCommunicator, 12345);
        httpServerImpl.start();
    }

    @After
    public void tearDown() throws Exception {
        httpServerImpl.stop();
    }

    @Test
    public void testGet() throws Exception {
        httpCommunicator.replyContent = "reply";

        final HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://127.0.0.1:12345/home").openConnection();
        final String response = new String(readStream(urlConnection.getInputStream()));

        httpCommunicator.waitUntilComplete(5);
        urlConnection.disconnect();

        assertEquals("reply", response);
        assertEquals("GET", httpCommunicator.method);
        assertEquals("/home", httpCommunicator.path);
    }

    @Test
    public void testGetWithParams() throws Exception {
        httpCommunicator.replyContent = "reply";

        final HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://127.0.0.1:12345/home?value=1&vv=ove&vv=nso").openConnection();
        final String response = new String(readStream(urlConnection.getInputStream()));

        httpCommunicator.waitUntilComplete(5);
        urlConnection.disconnect();

        assertEquals("reply", response);
        assertEquals("GET", httpCommunicator.method);
        assertEquals("/home", httpCommunicator.path);
        assertEquals(2, httpCommunicator.params.size());
        assertEquals("value", httpCommunicator.params.get(0).getParam());
        assertEquals(1, httpCommunicator.params.get(0).getValues().size());
        assertEquals("1", httpCommunicator.params.get(0).getValues().get(0));
        assertEquals(2, httpCommunicator.params.get(1).getValues().size());
        assertEquals("vv", httpCommunicator.params.get(1).getParam());
        assertEquals("ove", httpCommunicator.params.get(1).getValues().get(0));
        assertEquals("nso", httpCommunicator.params.get(1).getValues().get(1));
    }

    @Test
    public void testPost() throws Exception {
        httpCommunicator.replyContent = "reply";

        final HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://127.0.0.1:12345/more").openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        WriteStream.write(urlConnection.getOutputStream(), "write");
        final String response = new String(readStream(urlConnection.getInputStream()));

        httpCommunicator.waitUntilComplete(5);
        urlConnection.disconnect();

        assertEquals("reply", response);
        assertEquals("POST", httpCommunicator.method);
        assertEquals("/more", httpCommunicator.path);
    }

    private class TestHttpCommunicator implements HttpCommunicator {
        private String method;
        private String path;
        private List<HttpParamValues> params;
        private List<HttpParamValues> headers;
        private boolean completed;
        private String totalContent;
        private final Object completedSync = new Object();
        private String replyContent;

        @Override
        public AsyncContent sendMessageWithAsyncContent(String method, String path, List<HttpParamValues> params, List<HttpParamValues> headers) {
            this.method = method;
            this.path = path;
            this.params = params;
            this.headers = headers;
            return new TestAsyncContent();
        }

        public void waitUntilComplete(long timoutSec) throws InterruptedException, TimeoutException {
            synchronized (completedSync) {
                if (completed)
                    return;
                wait(timoutSec*1000);
                if (!completed)
                    throw new TimeoutException();
            }
        }

        private class TestAsyncContent implements AsyncContent {
            @Override
            public void pushContent(String content) {
                totalContent += content;
            }

            @Override
            public void pushContent(InputStream content) {
                try {
                    pushContent(new String(readStream(content)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void pushContent(ByteBuf content) {
                byte[] bytes = new byte[content.readableBytes()];
                content.readBytes(bytes);
                pushContent(new String(bytes));
            }

            @Override
            public AsyncReply completed() {
                synchronized (completedSync) {
                    completed = true;
                    completedSync.notifyAll();
                }
                return (consumeReplyContent, failed) -> {
                    consumeReplyContent.accept(new AsyncReplyContent() {
                        @Override
                        public String getContent() {
                            return replyContent;
                        }

                        @Override
                        public Optional<String> token() {
                            return Optional.of("TOUÖKJHWUHSD");
                        }
                    });
                };
            }

            @Override
            public void exceptionCaught(Throwable cause) {
                cause.printStackTrace();
            }
        }
    }
}