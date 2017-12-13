package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.utils.ReadStream;
import feature.utils.RestCommunicator;
import feature.utils.TestCore;

import java.io.IOException;
import java.io.InputStream;

import static feature.utils.ReadStream.createStringFromReader;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BatchSteps {

    TestCore testCore;

    @Before public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName()).get();}
    @After public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop(); }

    @When("^I send (.*) to batch$")
    public void sendFileToBatch(String filenameToBatch) throws Throwable {
        RestCommunicator restCommunicator = testCore.createRestCommunicator();
        String fileContents = loadFileContentsAsString(filenameToBatch);
        final RestCommunicator.Response<String> response = restCommunicator
                .post("/batch", new RestCommunicator.JsonPayload(fileContents), createStringFromReader);
        testCore.setResponse(response);
    }

    private String loadFileContentsAsString(String resourceFileName) throws IOException {
        final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resourceFileName);
        if (resourceAsStream == null)
            throw new IOException("Resource not found '"+resourceFileName+"'");
        return new String(ReadStream.readStream(resourceAsStream));
    }


    @Then("^the response is ok$")
    public void theResponseIsOk() throws Throwable {
        if (!testCore.responseIsOk())
            fail(testCore.responseText());
    }

    @Then("^the response is fail$")
    public void theResponseIsFail() throws Throwable {
        if (testCore.responseIsOk())
            fail("Should fail ");
    }

    @Then("^the response is fail with (.*) as error$")
    public void theResponseIsFailWithText(String errorText) throws Throwable {
        if (testCore.responseIsOk())
            fail("Should fail ");
        assertTrue(testCore.responseText().startsWith(errorText));
    }

}
