package org.objectagon.core.rest2.service.locator;

import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;

/**
 * Created by christian on 2017-02-22.
 */
public class RestKeyCheckImpl implements RestServiceActionLocator.RestKeyCheck {
    RestServiceProtocol.Method method;
    RestServiceActionLocator.RestPathPattern restPathPattern;

    public RestKeyCheckImpl(RestServiceProtocol.Method method, RestServiceActionLocator.RestPathPattern restPathPattern) {
        this.method = method;
        this.restPathPattern = restPathPattern;
    }

    @Override
    public RestServiceActionLocator.RestMatchRating check(RestServiceProtocol.Method method, RestServiceActionLocator.RestPath restPath) {
        if (!this.method.equals(method))
            return RestServiceActionLocator.RestMatchRating.None;
        return restPathPattern.check(restPath);
    }
}
