package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import feature.util.RelationManager;
import feature.util.TestCore;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationSteps {

    TestCore testCore;

    @Before
    public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName());}
    @After
    public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @When("^I add (.*) to (.*) with relation (.*)$")
    public void iAddOrderRowToOrderWithRelationOrderToOrderRow(String relationTo, String relationFrom, String relationAlias) throws Throwable {
        RelationManager mgr = RelationManager.create(testCore.createTestUser("Developer"));
        Instance.InstanceIdentity fromInstance = testCore.getNamedAddress(relationFrom);
        Instance.InstanceIdentity toInstance = testCore.getNamedAddress(relationTo);
        RelationClass.RelationClassIdentity relationClassIdentity = testCore.getNamedAddress(relationAlias);
        Relation.RelationIdentity relationIdentity = mgr.addRelation(fromInstance, toInstance, relationClassIdentity);

        assertThat(relationIdentity.getInstanceIdentity(RelationClass.RelationDirection.RELATION_TO), is(equalTo(toInstance)));
        assertThat(relationIdentity.getInstanceIdentity(RelationClass.RelationDirection.RELATION_FROM), is(equalTo(fromInstance)));
    }
}
