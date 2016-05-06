package org.objectagon.core.rest;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by christian on 2016-05-05.
 */
public interface ProcessorLocator {

    LocatorBuilder locatorBuilder();

    interface Locator {
        Optional<RestProcessor> match(Iterator<String> path, RestProcessor.Operation operation);
    }

    interface LocatorBuilder {
        PatternBuilder patternBuilder(RestProcessor restProcessor);
        Locator build();
    }

    interface PatternBuilder {
        PatternBuilder setOperation(RestProcessor.Operation operation);
        PatternBuilder add(String staticName);
        PatternBuilder addName();
        PatternBuilder addIdentity();
    }


}
