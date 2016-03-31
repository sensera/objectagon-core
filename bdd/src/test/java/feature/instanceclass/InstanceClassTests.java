package feature.instanceclass;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by christian on 2016-03-16.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features =
{
    "bdd/src/test/resources/feature/instanceclass"
})
public class InstanceClassTests { }
