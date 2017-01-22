package org.objectagon.core.command;

import org.objectagon.core.msg.Name;

/**
 * Created by christian on 2017-01-15.
 */
public interface Command {

    ServiceCommand attacheTo(ServiceName serviceName);
    ServiceCreator getServiceCreator();

    interface ServiceName extends Name {}

    interface ServiceCommand {
        ServiceName getServiceName();
    }

    interface ServiceCreator {
        void setServiceName(String serviceName);
        void setServiceType(String serviceType);
        ServiceCommand create();
    }
}
