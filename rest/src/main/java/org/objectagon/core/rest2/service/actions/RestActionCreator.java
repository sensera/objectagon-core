package org.objectagon.core.rest2.service.actions;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.object.InstanceProtocol;
import org.objectagon.core.rest2.service.RestServiceActionLocator;
import org.objectagon.core.rest2.service.RestServiceProtocol;
import org.objectagon.core.rest2.service.locator.RestServiceActionLocatorImpl;
import org.objectagon.core.service.Service;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2017-02-26.
 */
public class RestActionCreator {
    protected RestServiceActionLocatorImpl restServiceActionLocator;
    protected Service.ServiceName target;

    public RestActionCreator(RestServiceActionLocatorImpl restServiceActionLocator, Service.ServiceName target) {
        this.restServiceActionLocator = restServiceActionLocator;
        this.target = target;
    }

    protected <E extends Identity> E getIdAtIndex(int index, RestServiceActionLocator.RestPath restPath) {
        return restPath.values()
                .filter(restPathValue -> restPathValue.getIndex() == index)
                .findAny()
                .orElseThrow(() -> new SevereError(ErrorClass.UNKNOWN, ErrorKind.NOT_IMPLEMENTED))
                .getIdentity()
                .map(identity -> (E) identity)
                .orElseThrow(() -> new SevereError(ErrorClass.REST_SERVICE, ErrorKind.ALIAS_NOT_FOUND, restPath.valueAtIndex(index)));
    }

    public void addRestAction(
            RestAction restAction,
            AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send> send) {
        restServiceActionLocator.addRestAction(
                createRestAction(restAction, restAction.getProtocol(), send),
                restAction.getMethod(),
                restAction);
    }

    protected RestServiceActionLocator.RestAction createRestAction(
            Task.TaskName taskName,
            Protocol.ProtocolName protocolName,
            AbstractSessionRestAction.CreateSendMessageAction<InstanceProtocol.Send> send) {
        return new AbstractSessionRestAction<>(taskName, protocolName, target, send);
    }

    public interface RestAction extends Task.TaskName, RestServiceActionLocator.RestPathPattern {
        RestServiceProtocol.Method getMethod();
        Protocol.ProtocolName getProtocol();
    }

}
