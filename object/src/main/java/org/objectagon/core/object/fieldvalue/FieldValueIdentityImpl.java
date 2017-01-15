package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Field;
import org.objectagon.core.object.FieldValue;
import org.objectagon.core.object.Instance;

/**
 * Created by christian on 2016-02-28.
 */
public class FieldValueIdentityImpl extends StandardAddress implements FieldValue.FieldValueIdentity {

    private Instance.InstanceIdentity instanceIdentity;
    private Field.FieldIdentity fieldIdentity;

    @Override public Instance.InstanceIdentity getInstanceIdentity() {
        return instanceIdentity;
    }
    @Override public Field.FieldIdentity getFieldIdentity() {
        return fieldIdentity;
    }

    public FieldValueIdentityImpl(Instance.InstanceIdentity instanceIdentity, Field.FieldIdentity fieldIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceIdentity = instanceIdentity;
        this.fieldIdentity = fieldIdentity;
    }

    @Override
    public String toString() {
        return "FieldValueIdentityImpl{" +
                "instanceIdentity=" + instanceIdentity +
                ", fieldIdentity=" + fieldIdentity +
                "} " + super.toString();
    }
}
