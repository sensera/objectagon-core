package org.objectagon.core.rest;

import org.objectagon.core.msg.Address;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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
        PatternBuilder addIdentity(String alias);
    }

    interface LocatorContext {
        Iterator<String> path();
        RestProcessor.Operation operation();
        Optional<Address> findAlias(String name);
        Stream<Map.Entry<Integer,Address>> getStoredFoundAlias();
        void foundAlias(String value, Address foundAlias);
        LocatorContext next();
    }

    interface LocatorResponse {
        Optional<RestProcessor> restProcessor();
        Optional<Address> foundMatchingAlias(int index);
    }

}
