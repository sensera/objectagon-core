package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import feature.util.TaskWait;
import feature.util.TestCore;
import org.objectagon.core.msg.Message;
import org.objectagon.core.object.InstanceClass;

import java.util.Optional;

/**
 * Created by christian on 2016-03-31.
 */
public class InstanceSteps {
    TestCore testCore;

    @Before
    public void setUp(Scenario scenario) {testCore = TestCore.get(scenario.getName());}
    @After
    public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @When("^I create an instance from type: (.*)$")
    public void i_create_an_instance_from_type(String typeName) throws Throwable {
        Optional<TestCore.TestUser> developer = testCore.getLatestTestUser();
        Optional<Message.Value> instanceClassIdentity = developer.get().getValue(InstanceClass.INSTANCE_CLASS_IDENTITY);

        Message message = TaskWait.create(developer.get().createInstanceClassProtocolSend(instanceClassIdentity.get().asAddress()).createInstance()).startAndWait(50000L);
        developer.get().storeResponseMessage(message);
    }

}
