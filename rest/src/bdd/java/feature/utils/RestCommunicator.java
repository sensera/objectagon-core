package feature.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class RestCommunicator {
    private URL base;
    private Map<String,String> headerValues = new HashMap<>();

    public RestCommunicator(URL base) {
        this.base = base;
    }

    public <D extends String> Response<D> get(String path,
                                              Function<Reader, D> createDataFromStream) throws IOException {
        return send(new GetRequest<>(path), createDataFromStream);
    }

    public <D extends String> Response<D> put(String path,
                                              Payload payload,
                                              Function<Reader, D> createDataFromStream) throws IOException {
        return send(new PutRequest<>(path, payload), createDataFromStream);
    }

    public <D extends String> Response<D> post(String path,
                                              Payload payload,
                                              Function<Reader, D> createDataFromStream) throws IOException {
        return send(new PostRequest<>(path, payload), createDataFromStream);
    }

    public <D extends String, P extends Payload> Response<D> send(
            Request<P> request,
            Function<Reader, D> createDataFromStream) throws IOException {
        URL requestUrl = new URL(base, request.getPath());
        final HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();

        BufferedReader reader = null;

        try {
            prepareConnection(request, urlConnection);

            urlConnection.connect();

            request.getPayload().ifPresent(writePayload(urlConnection));

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            return new DataResponse<D>(createDataFromStream.apply(reader));
        } catch (IOException e) {
            //e.printStackTrace();
            return new FailedResponse<>(e.getMessage());
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private <P extends Payload> void prepareConnection(Request<P> request, HttpURLConnection urlConnection) throws ProtocolException {
        urlConnection.setRequestMethod(request.getMethod());
        urlConnection.setDoOutput(request.doOutput());
        urlConnection.setDoInput(request.doInput());
        request.getPayload()
                .flatMap(Payload::getFixedLength)
                .ifPresent(urlConnection::setFixedLengthStreamingMode);
        urlConnection.setReadTimeout(15*1000);
        headerValues.forEach(urlConnection::setRequestProperty);
    }

    private <P extends Payload> Consumer<P> writePayload(HttpURLConnection urlConnection) {
        return payload -> {
            try {
                payload.writeTo(urlConnection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public void addHeader(String headerName, String headerValue) {
        headerValues.put(headerName, headerValue);
    }

    public void addTraceHeader() {
        addHeader("trace","trace");
    }


    public interface Request<P extends Payload> {
        String getPath();
        String getMethod();
        Optional<P> getPayload();
        boolean doOutput();
        boolean doInput();
    }

    public class GetRequest<P extends Payload> implements Request<P> {
        private String path;

        public String getMethod() {return "GET";}

        public GetRequest(String path) {
            this.path = path;
        }

        public String getPath() {return path;}
        public Optional<P> getPayload() {return Optional.empty();}
        public boolean doOutput() {return false;}
        public boolean doInput() {return true;}
    }

    public class PostRequest<P extends Payload> implements Request<P> {
        P payload;
        private String path;

        public String getMethod() {return "POST";}

        public PostRequest(String path, P payload) {
            this.path = path;
            this.payload = payload;
        }

        public String getPath() {return path;}
        public Optional<P> getPayload() {return Optional.ofNullable(payload);}
        public boolean doOutput() {return true;}
        public boolean doInput() {return payload != null;}
    }

    public class PutRequest<P extends Payload> implements Request<P> {
        P payload;
        private String path;

        public String getMethod() {return "PUT";}

        public PutRequest(String path, P payload) {
            this.path = path;
            this.payload = payload;
        }

        public String getPath() {return path;}
        public Optional<P> getPayload() {return Optional.ofNullable(payload);}
        public boolean doOutput() {return true;}
        public boolean doInput() {return payload != null;}
    }

    public interface Payload {
        void writeTo(OutputStream inputStream) throws IOException;
        Optional<Long> getFixedLength();
    }

    public static class JsonPayload implements Payload {
        String json;

        public JsonPayload(String json) {
            this.json = json;
        }

        @Override public void writeTo(OutputStream outputStream) throws IOException {
            outputStream.write(json.getBytes());
        }

        @Override public Optional<Long> getFixedLength() {
            return Optional.of((long) json.getBytes().length);
        }
    }

    public static class MultipartPayload implements Payload {
        String data;

        public MultipartPayload(String data) {
            this.data = data;
        }

        @Override public void writeTo(OutputStream outputStream) throws IOException {
            outputStream.write(data.getBytes());
        }

        @Override public Optional<Long> getFixedLength() {
            return Optional.of((long) data.getBytes().length);
        }
    }

    public interface Response<Data> {

        default Optional<Data> getData() { return Optional.empty(); }
        default boolean ok() { return true; }
        default Optional<String> getErrorMessage() { return Optional.empty(); }
    }

    public class DataResponse<Data> implements Response<Data>{
        Data data;
        public DataResponse(Data data) {this.data = data;}
        public  Optional<Data> getData() {return Optional.ofNullable(data);}
    }

    public class FailedResponse<Data> implements Response<Data>{
        private String errorMessage;
        public FailedResponse(String errorMessage) {this.errorMessage = errorMessage;}
        public  Optional<Data> getData() {return Optional.empty();}
        @Override public Optional<String> getErrorMessage() {return Optional.of(errorMessage);}
        @Override public boolean ok() {return false;}
    }

/*
    public static Function<InputStream, Object> createJsonDataFromStream = reader -> {
        try {
            return new ObjectMapper().readValue(reader, Data.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    };
*/
}

