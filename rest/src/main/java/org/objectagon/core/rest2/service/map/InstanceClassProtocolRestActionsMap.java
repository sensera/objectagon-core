package org.objectagon.core.rest2.service.map;

import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceActionLocator.RestPathPattern;
import org.objectagon.core.rest2.service.RestServiceProtocol;

import static org.objectagon.core.rest2.service.locator.RestPathPatternBuilderImpl.base;

/**
 * Created by christian on 2017-02-26.
 */
public interface InstanceClassProtocolRestActionsMap<E, A extends InstanceClassProtocolRestActionsMap.InstanceClassAction> extends RestActionsMap<E, A> {

    /*
| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /class/                       | List classes        | Create new class  | -               | -                |
| /class/{id}                   | Get class info      | -                 | Update class    | -                |
| /class/{id}/name              | Get class name      | -                 | Set class name  | -                |
| /class/{id}/field             | List fields         | Create new field  | -               | -                |
| /class/{id}/relation          | Get relations       | Create new relation | -             | -                |
| /class/{id}/relation/{classId} |                    | Create new relation | -             | -                |
| /class/{id}/method            | List methods        |                   |                 | -                |
| /class/{id}/method/{id}       |                     | Attache method    |                 | -                |
| /class/{id}/instance          | -                   | Create new instance | -             | -                |
| /class/{id}/instance/{name}   | get instanceAlias   | Create new alias  | -               | remove alias     |

    */

    RestPathPattern CLASSES_PATH              = base("class").build();                                 // /instance/
    RestPathPattern CLASS_ID_PATH            = base("class").id().build();                            // /instance/[ID]/
    RestPathPattern CLASS_NAME_PATH          = base("class").name().build();                          // /instance/[NAME]/
/*
    RestPathPattern INSTANCE_FIELD_PATH         = base("instance").id().text("field").name().build();       // /instance/[ID]/field/[NAME]/
    RestPathPattern INSTANCE_RELATION_PATH      = base("instance").id().text("relation").id().build();      // /instance/[ID]/field/[NAME]/
    RestPathPattern INSTANCE_RELATION_ID_PATH   = base("instance").id().text("relation").id().id().build(); // /instance/[ID]/field/[NAME]/
    RestPathPattern INSTANCE_METHOD_PATH        = base("instance").id().text("method").name().build();      // /instance/[ID]/field/[NAME]/
*/

    enum InstanceClassAction implements Action {
        LIST_CLASSES    (RestServiceProtocol.Method.GET,    CLASSES_PATH);
/*
        LIST_INSTANCES( RestServiceProtocol.Method.GET,     INSTANCES_PATH),
        CREATE_INSTANCE(RestServiceProtocol.Method.PUT,     INSTANCE_NAME_PATH),
        GET_INSTANCE(   RestServiceProtocol.Method.GET,     INSTANCE_ID_PATH),
        DELETE_INSTANCE(RestServiceProtocol.Method.DELETE,  INSTANCE_ID_PATH),
        GET_VALUE(      RestServiceProtocol.Method.GET,     INSTANCE_FIELD_PATH),
        ADD_VALUE(      RestServiceProtocol.Method.PUT,     INSTANCE_FIELD_PATH),
        UPDATE_VALUE(   RestServiceProtocol.Method.POST,    INSTANCE_FIELD_PATH),
        REMOVE_VALUE(   RestServiceProtocol.Method.DELETE,  INSTANCE_FIELD_PATH),
        GET_RELATION(   RestServiceProtocol.Method.GET,     INSTANCE_RELATION_PATH),
        DELETE_RELATION(RestServiceProtocol.Method.DELETE,  INSTANCE_RELATION_PATH),
        SET_RELATION(   RestServiceProtocol.Method.POST,    INSTANCE_RELATION_ID_PATH),
        ADD_RELATION(   RestServiceProtocol.Method.PUT,     INSTANCE_RELATION_ID_PATH),
        INVOKE_METHOD(  RestServiceProtocol.Method.PUT,     INSTANCE_METHOD_PATH);
*/

        RestServiceProtocol.Method method;
        RestPathPattern restPathPattern;

        InstanceClassAction(RestServiceProtocol.Method method, RestPathPattern restPathPattern) {
            this.method = method;
            this.restPathPattern = restPathPattern;
        }

        @Override public String getName() { return name(); }
        @Override public RestServiceActionLocator.RestMatchRating check(RestServiceActionLocator.RestPath restPath) {
            return restPathPattern.check(restPath);
        }
        @Override public RestServiceProtocol.Method getMethod() {
            return method;
        }
    }
}
