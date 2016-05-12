package org.objectagon.core.rest;

import org.objectagon.core.msg.Address;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by christian on 2016-05-05.
 */
public interface ProcessorLocator {

    LocatorBuilder locatorBuilder();

    interface Locator {
        LocatorResponse match(LocatorContext locatorContext);
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

    interface LocatorContext {
        Iterator<String> path();
        RestProcessor.Operation operation();
        Optional<Address> findAlias(String name);
        Optional<Address> getStoredFoundAlias();
        void foundAlias(String value, Address foundAlias);
    }

    interface LocatorResponse {
        Optional<RestProcessor> restProcessor();
        Optional<Address> foundMatchingAlias();
    }

}
