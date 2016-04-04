package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import feature.util.TaskWait;
import feature.util.TestCore;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.object.InstanceClass;

/**
 * Created by christian on 2016-03-16.
 */
public class InstanceClassSteps {

    TestCore testCore;

    @Before public void setUp(Scenario scenario) {testCore = TestCore.get(scenario.getName());}
    @After public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @Given("^there is a type called (.*)$")
    public void prepareTypeWithName(String typeName) throws UserException {
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        Message message = TaskWait.create(developer.createInstanceClassEntityServiceProtocol().create()).startAndWait(1000l);
        developer.storeResponseMessage(message);
        developer.setValue(InstanceClass.INSTANCE_CLASS_IDENTITY, message.getValue(StandardField.ADDRESS));
    }

    @Given("^I create a type called: (.*)$")
    public void creteTypeWithName(String typeName) throws UserException {
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        Message message = TaskWait.create(developer.createInstanceClassEntityServiceProtocol().create()).startAndWait(1000l);
        developer.storeResponseMessage(message);
        developer.setValue(InstanceClass.INSTANCE_CLASS_IDENTITY, message.getValue(StandardField.ADDRESS));
    }

}
