package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.batch.BatchServiceProtocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.base;

/**
 * Created by christian on 2017-02-26.
 */
public interface BatchServiceProtocolRestActionsMap<A extends BatchServiceProtocolRestActionsMap.BatchAction> extends RestActionsMap<BatchServiceProtocol.Send, A> {

    RestPathPattern BATCH_PATH                = base("batch").build();

    enum BatchAction implements Action {
        BATCH_JOB( RestServiceProtocol.Method.POST, BATCH_PATH),
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        BatchAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
            this.method = method;
            this.restPathPattern = restPathPattern;
        }

        //@Override public String getName() { return name(); }

        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
        @Override public Protocol.ProtocolName getProtocol() {return BatchServiceProtocol.BATCH_SERVICE_PROTOCOL;}
    }
}
