package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import feature.utils.RestCommunicator;
import feature.utils.TestCore;

import static feature.utils.ReadStream.createStringFromReader;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class RestStepdefs {

    private static String FIND_INSTANCE_BY_ALIAS_PATH = "class/%s/instancename/%s";
    private static String FIND_CLASS_PATH = "class/%s";
    private static String LIST_FIELDS_BY_CLASS_PATH = "class/%s/field";
    private static String FIELD_VALUE = "instance/%s/field/%s";
    private static String INSTANCE_RELATION_VALUE = "instance/%s/relation/%s/";

    TestCore testCore;

    @Before public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName()).get();}
    @After public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop(); }

    @Then("^there is an instance with alias (.*) in (.*) class$")
    public void thereIsAnInstanceNamedMainInMainClass(String instanceName, String className) throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        final String path = String.format(FIND_INSTANCE_BY_ALIAS_PATH, className, instanceName);
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Instance "+instanceName+" not found in class "+className+" because "+stringResponse.getErrorMessage().get());
        }
        assertTrue(stringResponse.getData().get()+" should contain 'InstanceIdentityImpl'",stringResponse.getData().get().contains("InstanceIdentityImpl"));
    }


    @And("^the value of instance (.*) for field (.*) is (.*)$")
    public void theValueOfInstancePersonClassForFieldPersonClassNameIsFlabbba(
            String instanceAlias, String fieldAlias, String value) throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        final String path = String.format(FIELD_VALUE, instanceAlias, fieldAlias);
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Instance "+instanceAlias+" not found in field "+fieldAlias+" because "+stringResponse.getErrorMessage().get());
        }
        assertTrue(stringResponse.getData().get()+" should contain "+value,stringResponse.getData().get().contains("\""+value+"\""));
    }

    @And("^there is a class named (.*)$")
    public void thereIsAClassNamedPersonClass(String className) throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        final String path = String.format(FIND_CLASS_PATH, className);
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Class "+className+" not found because "+stringResponse.getErrorMessage().get());
        }
        assertTrue(stringResponse.getData().get().contains("InstanceClassIdentityImpl"));
    }

    @And("^there is an field with name (.*) in (.*) class$")
    public void thereIsAnFieldWithNameFieldInPersonClassClass(String fieldName, String className) throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        final String path = String.format(LIST_FIELDS_BY_CLASS_PATH, className);
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Class "+className+" not found because "+stringResponse.getErrorMessage().get());
        }
        assertTrue(stringResponse.getData().get().contains("FieldIdentityImpl"));
    }

    @And("^there is a relation (.*) between (.*) and (.*)$")
    public void theRelationMainPersonRelationIsBetweenMainAndPerson(String relationName, String instanceName1,
                                                                    String instanceName2) throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        final String path = String.format(INSTANCE_RELATION_VALUE, instanceName1, relationName);
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Relation "+relationName+" not found because "+stringResponse.getErrorMessage().get());
        }
        assertTrue("Should find 'InstanceIdentityImpl' in response "+stringResponse.getData().get(),stringResponse.getData().get().contains("InstanceIdentityImpl"));
    }
}
