package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import feature.util.InstanceClassManager;
import feature.util.TestCore;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.object.InstanceClass;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by christian on 2016-03-16.
 */
public class InstanceClassSteps {

    TestCore testCore;

    @Before public void setUp(Scenario scenario) {testCore = TestCore.get(scenario.getName());}
    @After public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @Given("^there is a type called (.*)$")
    public void prepareTypeWithName(String typeName) throws UserException {
        createTypeWithName(typeName);
    }

    @Given("^I create a type called: (.*)$")
    public void createTypeWithName(String typeName) throws UserException {
        InstanceClassManager mgr = InstanceClassManager.create(testCore.createTestUser("Developer"));
        InstanceClass.InstanceClassIdentity instanceClassIdentity = mgr.createInstanceClass();
        mgr.setInstanceClassName(instanceClassIdentity, typeName);
        testCore.storeNamedAddress(typeName, instanceClassIdentity);
    }

    @Given("^I search for an type called: (.*)$")
    public void findTypeWithName(String typeName) throws UserException {
        InstanceClassManager mgr = InstanceClassManager.create(testCore.createTestUser("Developer"));
        InstanceClass.InstanceClassIdentity instanceClassWithName = mgr.findInstanceClassWithName(typeName);
        testCore.storeNamedAddress(typeName, instanceClassWithName);
        assertThat(instanceClassWithName != null, is(equalTo(true)));
    }


    @When("^I add type (.*) instance alias (.*) for instance named: (.*)$")
    public void iAddInstanceClassItemInstanceAliasITEM_ALIASForInstanceNamedITEM(String typeName, String instanceAliasName, String instanceName) throws Throwable {
        InstanceClassManager mgr = InstanceClassManager.create(testCore.createTestUser("Developer"));
        mgr.setInstanceClassInstanceAliasName(testCore.getNamedAddress(typeName), instanceAliasName, testCore.getNamedAddress(instanceName));
    }
}
