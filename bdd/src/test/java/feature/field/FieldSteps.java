package feature.field;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import feature.util.TaskWait;
import feature.util.TestCore;
import org.objectagon.core.msg.Message;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.field.FieldNameImpl;

import java.util.Optional;

/**
 * Created by christian on 2016-03-16.
 */
public class FieldSteps {

    TestCore testCore;

    @Before public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName());}
    @After public void tareDown(Scenario scenario) { /*testCore.stop();*/}

    @When("^I add field (.*) to (.*)$")
    public void addFiledWithNameToTypeWithName(String fieldName, String typeName) throws Exception {
        Optional<TestCore.TestUser> developer = testCore.getLatestTestUser();
        Optional<Message.Value> instanceClassIdentity = developer.get().getValue(InstanceClass.INSTANCE_CLASS_IDENTITY);

        Message message = TaskWait.create(developer.get().createInstanceClassProtocolSend(instanceClassIdentity.get().asAddress()).addField(FieldNameImpl.create(fieldName))).startAndWait(1000l);
        developer.get().storeResponseMessage(message);
    }
}
