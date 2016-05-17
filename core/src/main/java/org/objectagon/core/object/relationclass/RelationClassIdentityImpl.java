package org.objectagon.core.object.relationclass;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.InstanceClass;
import org.objectagon.core.object.RelationClass;

/**
 * Created by christian on 2015-10-18.
 */
public class RelationClassIdentityImpl extends StandardAddress implements RelationClass.RelationClassIdentity {

    private InstanceClass.InstanceClassIdentity instanceClassIdentityFrom;
    private InstanceClass.InstanceClassIdentity instanceClassIdentityTo;
    private RelationClass.RelationType relationType;

    public InstanceClass.InstanceClassIdentity getInstanceClassIdentityFrom() {
        return instanceClassIdentityFrom;
    }

    public InstanceClass.InstanceClassIdentity getInstanceClassIdentityTo() {
        return instanceClassIdentityTo;
    }

    @Override
    public RelationClass.RelationType getRelationType() {
        return relationType;
    }

    @Override
    public InstanceClass.InstanceClassIdentity getInstanceClassIdentity(RelationClass.RelationDirection relationDirection) {
        switch (relationDirection) {
            case RELATION_FROM: return instanceClassIdentityFrom;
            case RELATION_TO: return instanceClassIdentityTo;
            default: throw new SevereError(ErrorClass.UNKNOWN, ErrorKind.INCONSISTENCY);
        }
    }

    public RelationClassIdentityImpl(InstanceClass.InstanceClassIdentity instanceClassIdentityFrom, InstanceClass.InstanceClassIdentity instanceClassIdentityTo, RelationClass.RelationType relationType, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceClassIdentityFrom = instanceClassIdentityFrom;
        this.instanceClassIdentityTo = instanceClassIdentityTo;
        this.relationType = relationType;
    }

    @Override
    public String toString() {
        return "RelationClassIdentityImpl{" +
                "instanceClassIdentityFrom=" + instanceClassIdentityFrom +
                ", instanceClassIdentityTo=" + instanceClassIdentityTo +
                ", relationType=" + relationType +
                "} " + super.toString();
    }
}
