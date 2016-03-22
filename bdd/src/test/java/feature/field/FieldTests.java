package feature.field;

import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;
import cucumber.api.junit.Cucumber;

/**
 * Created by christian on 2016-03-16.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features =
{
    "bdd/src/test/resources/feature/field"
})
public class FieldTests { }
