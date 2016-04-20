package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.util.InstanceManager;
import feature.util.TestCore;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.InstanceClass;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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
        there_is_an_instance_created_from_type_named(typeName, "instance");
    }

    @When("^there is an instance created from type: (.*) named: (.*)$")
    public void there_is_an_instance_created_from_type_named(String typeName, String instanceName) throws Throwable {
        InstanceManager mgr = InstanceManager.create(testCore.createTestUser("Developer"));
        InstanceClass.InstanceClassIdentity instanceClassIdentity = testCore.getNamedAddress(typeName);
        Instance.InstanceIdentity instanceIdentity = mgr.createInstance(instanceClassIdentity);
        assertThat(instanceIdentity, is(not(equalTo(null))));
        testCore.storeNamedAddress(instanceName, instanceIdentity);
    }

    @When("^set instance: (.*) field: (.*) to value: (.*)$")
    public void setInstanceITEMFieldItemNameToValueGurra(String instanceName, String fieldName, String value) throws Throwable {
        InstanceManager mgr = InstanceManager.create(testCore.createTestUser("Developer"));
        Instance.InstanceIdentity instanceIdentity = testCore.getNamedAddress(instanceName);
        Field.FieldIdentity fieldIdentity = testCore.getNamedAddress(fieldName);
        mgr.setValue(instanceIdentity, fieldIdentity, MessageValue.text(value));
    }

    @Then("^the value of instance: (.*) field: (.*) is: (.*)$")
    public void theValueOfInstanceITEMFieldItemNameIsGurra(String instanceName, String fieldName, String value) throws Throwable {
        InstanceManager mgr = InstanceManager.create(testCore.createTestUser("Developer"));
        Instance.InstanceIdentity instanceIdentity = testCore.getNamedAddress(instanceName);
        Field.FieldIdentity fieldIdentity = testCore.getNamedAddress(fieldName);
        Message.Value valueForTest = mgr.getValue(instanceIdentity, fieldIdentity);
        assertThat(valueForTest.asText(), is(equalTo(value)));
    }
}
