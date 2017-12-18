package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.utils.RestCommunicator;
import feature.utils.TestCore;

import java.util.Map;
import java.util.stream.Collectors;

import static feature.utils.ReadStream.createStringFromReader;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class RestStepdefs {

    private static String FIND_INSTANCE_BY_ALIAS_PATH = "class/%s/instancename/%s";
    private static String FIND_CLASS_PATH = "class/%s";
    private static String LIST_FIELDS_BY_CLASS_PATH = "class/%s/field";
    private static String FIELD_VALUE_PATH = "instance/%s/field/%s";
    private static String INSTANCE_RELATION_PATH = "instance/%s/relation/%s/";
    private static String INVOKE_METHOD_PATH = "/instance/%s/method/%s/";
    private static String ASSIGN_TRANSACTION_PATH = "/transaction/%s/assign/";
    private static String EXTEND_TRANSACTION_PATH = "/transaction/%s/extend/";
    private static String COMMIT_TRANSACTION_PATH = "/transaction/%s/commit/";
    private static String SET_METHOD_CODE_PATH = "/method/%s/code/";

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
        final String path = String.format(FIELD_VALUE_PATH, instanceAlias, fieldAlias);
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
        final String path = String.format(INSTANCE_RELATION_PATH, instanceName1, relationName);
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Relation "+relationName+" not found because "+stringResponse.getErrorMessage().get());
        }
        assertTrue("Should find 'InstanceIdentityImpl' in response "+stringResponse.getData().get(),stringResponse.getData().get().contains("InstanceIdentityImpl"));
    }

    @When("^method (.*) for instance (.*) is invoked with params$")
    public void methodAddToForInstancePersonIsInvokedWithParams(String methodName, String instanceAlias, Map<String,String> methodParams) throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        String path = String.format(INVOKE_METHOD_PATH, instanceAlias, methodName);
        String params = "";
        if (methodParams != null && !methodParams.isEmpty()) {
            params = "?" + methodParams.entrySet().stream()
                    .map(paramNameAndValue -> ""+paramNameAndValue.getKey()+"="+paramNameAndValue.getValue())
                    .collect(Collectors.joining("&"));
        }
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path+params, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Invoke method "+methodName+" of "+instanceAlias+" failed because "+stringResponse.getErrorMessage().get());
        }
        //assertTrue("Should find 'InstanceIdentityImpl' in response "+stringResponse.getData().get(),stringResponse.getData().get().contains("InstanceIdentityImpl"));
    }

    @When("^I shift to (.*)$")
    public void iShiftToKalle(String tokenName) throws Throwable {
        testCore.useToken(tokenName);
    }

    @When("^I assign root transaction to current token")
    public void iShiftToKalle2() throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        String path = String.format(ASSIGN_TRANSACTION_PATH, "1234567890");
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Assign transaction failed because "+stringResponse.getErrorMessage().get());
        }
        //assertTrue("Should find 'InstanceIdentityImpl' in response "+stringResponse.getData().get(),stringResponse.getData().get().contains("InstanceIdentityImpl"));
    }

    @And("^I extend current transaction$")
    public void iExtendCurrenTransaction() throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        String path = String.format(EXTEND_TRANSACTION_PATH, testCore.getTokenName());
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Extend transaction failed because "+stringResponse.getErrorMessage().get());
        }
    }

    @And("^I commit current transaction$")
    public void iCommitCurrentTransaction() throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        String path = String.format(COMMIT_TRANSACTION_PATH, testCore.getTokenName());
        final RestCommunicator.Response<String> stringResponse = restCommunicator.get(path, createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Commit transaction failed because "+stringResponse.getErrorMessage().get());
        }
    }

    @And("^Update method (.*) to (.*)$")
    public void iCommitCurrentTransaction(String methodName, String code) throws Throwable {
        final RestCommunicator restCommunicator = testCore.createRestCommunicator();
        String path = String.format(SET_METHOD_CODE_PATH, methodName);
        final RestCommunicator.Response<String> stringResponse = restCommunicator.post(path, new RestCommunicator.JsonPayload(code), createStringFromReader);
        if (!stringResponse.ok()) {
            fail("Set method failed because "+stringResponse.getErrorMessage().get());
        }
    }
}
