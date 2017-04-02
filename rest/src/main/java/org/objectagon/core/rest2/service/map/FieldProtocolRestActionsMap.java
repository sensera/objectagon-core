package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.object.FieldProtocol;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.urlPattern;

/**
 * Created by christian on 2017-02-26.
 */
public interface FieldProtocolRestActionsMap<A extends FieldProtocolRestActionsMap.FieldAction> extends RestActionsMap<FieldProtocol.Send, A> {
/*
| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /field/{id}                   | Field contents      | -                 | -               | Remove field     |
| /field/{id}/name              | Get field name      | -                 | Set field name  | -                |
| /field/{id}/type              | Get field type      | -                 | Set field type  | -                |
| /field/{id}/default           | Get field default value  | -            | Set field default value | -        |
*/
    ParamName TYPE_FIELD_PARAM = ParamName.create("type");
    ParamName DEFAULT_VALUE_FIELD_PARAM = ParamName.create("defaultValue");

    RestPathPattern FIELD_PATH =                urlPattern("/field/{id}");
    RestPathPattern FIELD_NAME_PATH =           urlPattern("/field/{id}/name");
    RestPathPattern FIELD_NAME_NAME_PATH =      urlPattern("/field/{id}/name/{name}");
    RestPathPattern FIELD_TYPE_PATH =           urlPattern("/field/{id}/type");
    RestPathPattern FIELD_DEFAULT_VALUE_PATH =  urlPattern("/field/{id}/default");

    enum FieldAction implements Action {
        FIELD(RestServiceProtocol.Method.GET,               FIELD_PATH),
        GET_NAME(RestServiceProtocol.Method.GET,            FIELD_NAME_PATH),
        SET_NAME(RestServiceProtocol.Method.POST,           FIELD_NAME_NAME_PATH),
        GET_TYPE(RestServiceProtocol.Method.GET,            FIELD_TYPE_PATH),
        SET_TYPE(RestServiceProtocol.Method.POST,           FIELD_TYPE_PATH),
        GET_DEFAULT_VALUE(RestServiceProtocol.Method.GET,   FIELD_DEFAULT_VALUE_PATH),
        SET_DEFAULT_VALUE(RestServiceProtocol.Method.POST,  FIELD_DEFAULT_VALUE_PATH),
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        FieldAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
            this.method = method;
            this.restPathPattern = restPathPattern;
        }

        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
        @Override public Protocol.ProtocolName getProtocol() {return FieldProtocol.FIELD_PROTOCOL;}
    }
}
