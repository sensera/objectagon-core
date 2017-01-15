package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import feature.util.MetaManager;
import feature.util.TestCore;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.object.Meta;

/**
 * Created by christian on 2016-03-16.
 */
public class MetaSteps {

    TestCore testCore;

    @Before
    public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName());}
    @After
    public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @Given("^there is a base called (.*)$")
    public void prepareTypeWithName(String typeName) throws UserException {
        createTypeWithName(typeName);
    }

    @Given("^I create a base called: (.*)$")
    public void createTypeWithName(String typeName) throws UserException {
        MetaManager mgr = MetaManager.create(testCore.createTestUser("Developer"));
        Meta.MetaIdentity metaIdentity = mgr.createMeta();
        testCore.storeNamedAddress(typeName, metaIdentity);
    }
}
