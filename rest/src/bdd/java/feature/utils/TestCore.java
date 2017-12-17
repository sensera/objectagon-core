package feature.utils;

import org.objectagon.core.rest2.RestServer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

public class TestCore {

    private static TestCores testCores = new TestCores();


    public static Supplier<TestCore> get(String name) {
        return testCores.get(name);
    }

    public void stop() {
        testCores.stop(name);
    }

    private String name;
    private RestServer restServer;
    private RestCommunicator.Response response;

    public TestCore(String name, RestServer restServer) {
        this.name = name;
        this.restServer = restServer;
    }

    public RestCommunicator createRestCommunicator(String token)  {
        try {
            final RestCommunicator restCommunicator = new RestCommunicator(new URL("http://localhost:" + restServer.getPort()));
            restCommunicator.addHeader("OBJECTAGON_REST_TOKEN", token);
            return restCommunicator;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public RestCommunicator createRestCommunicator()  {
        return createRestCommunicator("1234567890");
    }

    public boolean responseIsOk() {
        return response != null && response.ok();
    }

    public void setResponse(RestCommunicator.Response response) {
        this.response = response;
    }

    public String responseText() {
        return response != null ? (String) response.getErrorMessage().orElse("") : "";
    }

}
