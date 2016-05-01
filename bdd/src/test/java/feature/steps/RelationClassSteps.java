package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import feature.util.InstanceClassManager;
import feature.util.TestCore;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassSteps {

    TestCore testCore;

    @Before
    public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName());}
    @After
    public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @When("^there is an owning relation from (.*) to (.*) named (.*)$")
    public void thereIsAnAggregateRelationFromTo(String fromAliasClass, String toAliasClass, String relationClassAlias) throws Throwable {
        addRelationFromTo(RelationClass.RelationType.AGGREGATE, fromAliasClass, toAliasClass, relationClassAlias);
    }

    @When("^I add an owning relation from (.*) to (.*) named (.*)$")
    public void addAggregateRelationFromTo(String fromAliasClass, String toAliasClass, String relationClassAlias) throws Throwable {
        addRelationFromTo(RelationClass.RelationType.AGGREGATE, fromAliasClass, toAliasClass, relationClassAlias);
    }

    @When("^I add an knowing relation from (.*) to (.*) named (.*)$")
    public void addAssociationRelationFromTo(String fromAliasClass, String toAliasClass, String relationClassAlias) throws Throwable {
        addRelationFromTo(RelationClass.RelationType.ASSOCIATION, fromAliasClass, toAliasClass, relationClassAlias);
    }

    @When("^I add an extending relation from (.*) to (.*) named (.*)$")
    public void addInheritanceRelationFromTo(String fromAliasClass, String toAliasClass, String relationClassAlias) throws Throwable {
        addRelationFromTo(RelationClass.RelationType.INHERITANCE, fromAliasClass, toAliasClass, relationClassAlias);
    }

    private void addRelationFromTo(RelationClass.RelationType relationType, String fromAliasClass, String toAliasClass, String relationClassAlias) throws UserException {
        InstanceClassManager mgr = InstanceClassManager.create(testCore.createTestUser("Developer"));
        InstanceClass.InstanceClassIdentity fromInstanceClass = testCore.getNamedAddress(fromAliasClass);
        InstanceClass.InstanceClassIdentity toInstanceClass = testCore.getNamedAddress(toAliasClass);
        RelationClass.RelationClassIdentity relationClassIdentity = mgr.addRelation(relationType, fromInstanceClass, toInstanceClass);
        testCore.storeNamedAddress(relationClassAlias, relationClassIdentity);
    }
}
