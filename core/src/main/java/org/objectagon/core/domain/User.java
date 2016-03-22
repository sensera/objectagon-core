package org.objectagon.core.domain;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardEntity;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.storage.standard.StandardData;

/**
 * Created by christian on 2015-11-01.
 */
public class User extends StandardEntity<StandardData> {

    public static EntityName USER_ENTITY_NAME = EntityName.create("User");

    public static Message.Field USER_NAME = NamedField.text("userName");
    public static Message.Field PASSWORD = NamedField.password("password");

    public User(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }
}
