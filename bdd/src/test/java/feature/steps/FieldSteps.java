package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import feature.util.InstanceClassManager;
import feature.util.TestCore;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by christian on 2016-03-16.
 */
public class FieldSteps {

    TestCore testCore;

    @Before
    public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName());}
    @After
    public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @When("^I add field (.*) to (.*)$")
    public void addFiledWithNameToTypeWithName(String fieldName, String typeName) throws Throwable {
        the_type_has_a_field_named(typeName, fieldName);
    }

    @Given("^the type (.*) has a field named (.*)$")
    public void the_type_has_a_field_named(String typeName, String fieldName) throws Throwable {
        InstanceClassManager mgr = InstanceClassManager.create(testCore.createTestUser("Developer"));
        InstanceClass.InstanceClassIdentity instanceClassWithName = testCore.getNamedAddress(typeName);
        assertThat(instanceClassWithName != null, is(equalTo(true)));
        Field.FieldIdentity fieldIdentity = mgr.addFieldToInstanceClass(instanceClassWithName);
        assertThat(fieldIdentity != null, is(equalTo(true)));
        testCore.storeNamedAddress(fieldName, fieldIdentity);
    }

}
