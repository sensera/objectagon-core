package feature;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import feature.util.ResponseMessageName;
import feature.util.TestCore;

/**
 * Created by christian on 2016-03-16.
 */
public class CommonSteps {

    TestCore testCore;

    @Before public void setUp(Scenario scenario) {testCore = TestCore.get(scenario.getName());}
    @After public void tareDown(Scenario scenario) {testCore.stop();}

    @Then("^the response will be (.*)$")
    public void verifyResponseMessageName(ResponseMessageName responseMessageName) {
        testCore.getLatestTestUser().get().verifyResponseMessage(responseMessageName);
    }
}
