package org.objectagon.core.domain;

import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.receiver.BasicReceiverCtrl;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.msg.receiver.StandardReceiverCtrl;
import org.objectagon.core.service.AbstractService;
import org.objectagon.core.service.Service;
import org.objectagon.core.service.ServiceWorkerImpl;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.entity.EntityService;
import org.objectagon.core.storage.standard.StandardData;

/**
 * Created by christian on 2015-11-01.
 */
public class UserService extends EntityService<Identity, StandardData, UserService, EntityService.EntityServiceWorker> {

    public UserService(StandardReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

}
