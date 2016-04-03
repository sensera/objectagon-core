package org.objectagon.core.rest;

import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.Server;
import org.objectagon.core.msg.name.StandardName;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.utils.FindNamedConfiguration;

/**
 * Created by christian on 2016-02-11.
 */
public class RestService extends AbstractService<RestService.RestServiceWorker, StandardAddress> {

    public static final Name REST_SERVICE_NAME = StandardName.name("REST_SERVICE_NAME");

    public void registerAt(Server server) {
        server.registerFactory(REST_SERVICE_NAME, RestService::new);
    }

    public RestService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected RestServiceWorker createWorker(WorkerContext workerContext) {
        return new RestServiceWorker(workerContext);
    }

    @Override
    protected StandardAddress createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardAddress::standard);
    }

    public class RestServiceWorker extends ServiceWorkerImpl {
        public RestServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }
}
