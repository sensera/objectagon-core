package org.objectagon.core.storage.entity;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.server.AbstractFactory;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.standard.StandardData;
import org.objectagon.core.storage.standard.StandardEntity;
import org.objectagon.core.storage.standard.StandardIdentity;

/**
 * Created by christian on 2016-01-09.
 */
public class TestEntity extends StandardEntity<StandardData> {

    public static final EntityName ENTITY_NAME = EntityName.create("Test");

    public static void registerAtServer(Server server) {
        server.registerFactory(ENTITY_NAME, TestEntity::new);
    }

    public static Message.Field NAME = NamedField.text("name");
    public static Message.Field AGE = NamedField.password("age");

    public TestEntity(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

}
