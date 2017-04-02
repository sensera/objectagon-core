package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.object.MetaProtocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.urlPattern;

/**
 * Created by christian on 2017-02-26.
 */
public interface MetaProtocolRestActionsMap<A extends MetaProtocolRestActionsMap.MetaAction> extends RestActionsMap<MetaProtocol.Send, A> {
/*

    | URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
    |-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
    | /meta/                        | List meta           | Create meta       | -               | -                |
    | /meta/{id}                    | Get meta contents   | -                 | -               | Remove           |
    | /meta/{id}/method             | Get meta methods    | Create method     | -               |                  |

*/


    RestPathPattern META_PATH = urlPattern("/meta/");
    RestPathPattern META_METHOD_PATH = urlPattern("/meta/{id}/method/");

    enum MetaAction implements Action {
        CREATE_METHOD( RestServiceProtocol.Method.PUT, META_METHOD_PATH),
        //EXTEND_TRANSACTION
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        MetaAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
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
        @Override public Protocol.ProtocolName getProtocol() {return MetaProtocol.META_PROTOCOL;}
    }
}
