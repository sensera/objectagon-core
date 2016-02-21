package org.objectagon.core.domain;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardData;
import org.objectagon.core.storage.standard.StandardEntity;
import org.objectagon.core.storage.standard.StandardIdentity;

/**
 * Created by christian on 2015-11-01.
 */
public class User extends StandardEntity<StandardData> {

    public static EntityName USER_ENTITY_NAME = EntityName.create("User");

    public static Message.Field USER_NAME = NamedField.text("userName");
    public static Message.Field PASSWORD = NamedField.password("password");

    public User(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }
}
