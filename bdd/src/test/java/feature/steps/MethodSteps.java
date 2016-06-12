package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import feature.util.InstanceClassManager;
import feature.util.MetaManager;
import feature.util.MethodManager;
import feature.util.TestCore;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.Meta;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.method.ParamNameImpl;
import org.objectagon.core.utils.KeyValue;
import org.objectagon.core.utils.KeyValueUtil;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by christian on 2016-03-16.
 */
public class MethodSteps {

    TestCore testCore;

    @Before
    public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName());}
    @After
    public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}

    @When("^I add method (.*) to (.*)$")
    public void addFiledWithNameToTypeWithName(String methodName, String typeName) throws Throwable {
        the_type_has_a_field_named(typeName, methodName);
    }

    @Given("^the base (.*) has a method named (.*)$")
    public void the_type_has_a_field_named(String typeName, String methodName) throws Throwable {
        MetaManager mgr = MetaManager.create(testCore.createTestUser("Developer"));
        Meta.MetaIdentity metaIdentity = testCore.getNamedAddress(typeName);

        assertThat(metaIdentity != null, is(equalTo(true)));

        final Method.MethodIdentity methodIdentity = mgr.createMethodInMeta(metaIdentity);
        assertThat(methodIdentity != null, is(equalTo(true)));
        testCore.storeNamedAddress(methodName, methodIdentity);
    }
    @And("^I set the code of the method (.*) to (.*)$")
    public void theFieldItemNameHasDefaultValuePhone2(String methodAlias, String code) throws Throwable {
        theFieldItemNameHasDefaultValuePhone(methodAlias, code);
    }
    @And("^the method (.*) has code value (.*)$")
    public void theFieldItemNameHasDefaultValuePhone(String methodAlias, String code) throws Throwable {
        Method.MethodIdentity methodIdentity = testCore.getNamedAddress(methodAlias);
        MethodManager mgr = MethodManager.create(testCore.createTestUser("Developer"));
        mgr.setCode(methodIdentity, code);
    }

    @And("^the code value for (.*) is (.*)$")
    public void theDefaultValueForItemNameIsPhone(String methodAlias, String code) throws Throwable {
        Method.MethodIdentity methodIdentity = testCore.getNamedAddress(methodAlias);
        MethodManager mgr = MethodManager.create(testCore.createTestUser("Developer"));
        String codeRetrieved = mgr.getCode(methodIdentity);
        assertThat(codeRetrieved, is(equalTo(code)));
    }

    @And("the method (.*) is welded to (.*) without field")
    public void theDefaultValueForItemNameIsPhone3(String methodAlias, String typeName) throws Throwable {
        theDefaultValueForItemNameIsPhone2(methodAlias, typeName);
    }
    @And("^I weld method (.*) to type (.*)$")
    public void theDefaultValueForItemNameIsPhone2(String methodAlias, String typeName) throws Throwable {
        theDefaultValueForItemNameIsPhone4(methodAlias, null, typeName, null);
    }

    @And("^the method (.*) with parameter (.*) is welded to (.*) and field (.*)$")
    public void theDefaultValueForItemNameIsPhone4(String methodAlias, String paramName, String typeName, String fieldName) throws Throwable {
        MethodManager mgr = MethodManager.create(testCore.createTestUser("Developer"));
        Method.MethodIdentity methodIdentity = testCore.getNamedAddress(methodAlias);
        Field.FieldIdentity fieldIdentity = fieldName != null ? testCore.getNamedAddress(fieldName) : null;
        InstanceClass.InstanceClassIdentity instanceClass = testCore.getNamedAddress(typeName);
        InstanceClassManager mgr2 = InstanceClassManager.create(testCore.createTestUser("Developer"));
        List<KeyValue<Method.ParamName, Field.FieldIdentity>> fieldMappings = paramName != null
                ? KeyValueUtil.single(ParamNameImpl.create(paramName), fieldIdentity)
                : Collections.EMPTY_LIST;
        mgr2.addMethodToInstanceClass(instanceClass, methodIdentity, fieldMappings);
    }

    @And("^the method (.*) has a parameter called (.*)$")
    public void theMethodAddNumberHasAParameterCalledSumValue(String methodAlias, String paramName) throws Throwable {
        MethodManager mgr = MethodManager.create(testCore.createTestUser("Developer"));
        Method.MethodIdentity methodIdentity = testCore.getNamedAddress(methodAlias);
        mgr.addParam(methodIdentity, ParamNameImpl.create(paramName));
    }
}


