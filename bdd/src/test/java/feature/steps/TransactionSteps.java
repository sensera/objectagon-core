package feature.steps;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import feature.util.TestCore;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;

import java.util.stream.Stream;

/**
 * Created by christian on 2016-04-20.
 */
public class TransactionSteps  {

    TestCore testCore;

    @Before
    public void setUp(Scenario scenario) { testCore = TestCore.get(scenario.getName());}
    @After
    public void tareDown(Scenario scenario) { if (testCore!=null) testCore.stop();}


    @Given("^there is an active transaction$")
    public void createTransaction() throws UserException {
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        developer.createTransaction();
    }

    @Given("^there is an transaction named (.*)$")
    public void createTransaction(String transactionAlias) throws UserException {
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        Transaction transaction = developer.createTransaction();
        testCore.storeNamedAddress(transactionAlias, transaction);
    }

    @Given("^the active transaction has been commited$")
    public void theActiveTransactionHasBeenCommited() throws Throwable {
        commitTheActiveTransaction();
    }

    @When("^commit the active transaction$")
    public void commitTheActiveTransaction() throws Throwable {
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        developer.commitTransaction();
    }

    @When("^commit the transaction named (.*)$")
    public void commitTheActiveTransaction(String transactionAlias) throws Throwable {
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        Transaction transaction = testCore.getNamedAddress(transactionAlias);
        developer.commitTransaction(transaction);
    }

    @Given("^there is an user named (.*) with transaction (.*)$")
    public void thereIsAnUserNamedCalleWithTransactionTR(String userName, String transactionAlias) throws Throwable {
        Transaction transaction = testCore.getNamedAddress(transactionAlias);
        testCore.getTestUser(userName).ifPresent(user -> user.setTransaction(transaction));
    }

    @Given("^the active transaction contains (.*)$")
    public void theActiveTransactionContainsItem(String alias) throws Throwable {
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        Identity identity = testCore.getNamedAddress(alias);
        Stream<Identity> targets = developer.getTransactionTargets(developer.getActiveTransaction());
        targets.filter(identity1 -> identity1.equals(identity)).findAny().orElseThrow(() -> new Exception(alias + " not found in transaction!"));
    }

    @Given("^the active transaction is: (.*)$")
    public void theActiveTransactionIsSystemTransaction(String transactionAlias) throws Throwable {
        Transaction transaction = testCore.getNamedAddress(transactionAlias);
        testCore.setActiveTransaction(transaction);
        TestCore.TestUser developer = testCore.createTestUser("Developer");
        developer.setTransaction(transaction);
    }
}
