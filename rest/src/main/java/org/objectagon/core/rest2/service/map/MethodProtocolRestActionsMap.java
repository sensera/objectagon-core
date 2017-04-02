package org.objectagon.core.rest2.service.map;

import org.objectagon.core.msg.Protocol;
import org.objectagon.core.object.MethodProtocol;
import org.objectagon.core.rest2.service.ParamName;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.urlPattern;

/**
 * Created by christian on 2017-02-26.
 */
public interface MethodProtocolRestActionsMap<A extends MethodProtocolRestActionsMap.MethodAction> extends RestActionsMap<MethodProtocol.Send, A> {
/*
| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /method/{id}                  | Method contents     | -                 | -               | Remove method    |
| /method/{id}/code             | Get method code     | -                 | Set method code | -                |
| /method/{id}/param            | All params          | Create new param  | -               |                  |
| /method/{id}/param/{name}     | Get param contents  | -                 | -               | Remove param     |
*/
    ParamName TYPE_METHOD_PARAM = ParamName.create("type");
    ParamName DEFAULT_VALUE_METHOD_PARAM = ParamName.create("defaultValue");

    RestPathPattern METHOD_PATH =               urlPattern("/method/{id}");
    RestPathPattern METHOD_CODE_PATH =          urlPattern("/method/{id}/code/");
    RestPathPattern METHOD_PARAM_PATH =         urlPattern("/method/{id}/param/");
    RestPathPattern METHOD_PARAM_NAME_PATH =    urlPattern("/method/{id}/param/{name}");

    enum MethodAction implements Action {
        METHOD(RestServiceProtocol.Method.PUT,          METHOD_PATH),
        GET_CODE(RestServiceProtocol.Method.GET,        METHOD_CODE_PATH),
        SET_CODE(RestServiceProtocol.Method.POST,       METHOD_CODE_PATH),
        GET_ALL_PARAMS(RestServiceProtocol.Method.GET,  METHOD_PARAM_PATH),
        CREATE_PARAM(RestServiceProtocol.Method.PUT,    METHOD_PARAM_PATH),
        GET_PARAMS(RestServiceProtocol.Method.GET,      METHOD_PARAM_NAME_PATH),
        ;

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        MethodAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
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
        @Override public Protocol.ProtocolName getProtocol() {return MethodProtocol.METHOD_PROTOCOL;}
    }
}
