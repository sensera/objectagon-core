package feature;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by christian on 2016-03-19.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features =
{
    "rest/src/bdd/resources/features"
          //  /projects/objectagon/objectagon-core/rest/src/bdd/resources/features/
})
public class allTests {
}
