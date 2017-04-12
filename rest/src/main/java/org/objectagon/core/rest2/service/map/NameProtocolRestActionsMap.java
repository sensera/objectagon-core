package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.service.name.NameServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.urlPattern;

/**
 * Created by christian on 2017-02-26.
 */
public interface NameProtocolRestActionsMap<A extends NameProtocolRestActionsMap.NameAction> extends RestActionsMap<NameServiceProtocol.Send, A> {

    RestPathPattern NAME_PATH                = urlPattern("/name/{name}/");
    RestPathPattern NAME_ID_PATH                = urlPattern("/name/{name}/id/{id}/");

    enum NameAction implements Action {
        FIND(RestServiceProtocol.Method.GET,            NAME_PATH),
        UPDATE(RestServiceProtocol.Method.PUT,          NAME_ID_PATH),
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        NameAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
            this.method = method;
            this.restPathPattern = restPathPattern;
        }

        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
        @Override public Protocol.ProtocolName getProtocol() {return NameServiceProtocol.NAME_SERVICE_PROTOCOL;}

    }
}
