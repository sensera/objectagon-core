package org.objectagon.core.object.relation;

import org.objectagon.core.Server;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.relationclass.RelationDirectionUtil;

import java.util.Objects;

/**
 * Created by christian on 2016-03-07.
 */
public class RelationIdentityImpl extends StandardAddress implements Relation.RelationIdentity{
    private final RelationClass.RelationClassIdentity relationClassIdentity;
    private final Instance.InstanceIdentity instanceIdentityFrom;
    private final Instance.InstanceIdentity instanceIdentityTo;

    @Override
    public RelationClass.RelationClassIdentity getRelationClassIdentity() {
        return relationClassIdentity;
    }

    public Instance.InstanceIdentity getInstanceIdentityFrom() {
        return instanceIdentityFrom;
    }

    public Instance.InstanceIdentity getInstanceIdentityTo() {
        return instanceIdentityTo;
    }

    @Override
    public Instance.InstanceIdentity getInstanceIdentity(RelationClass.RelationDirection relationDirection) {
        return RelationDirectionUtil.create(relationDirection).getInstance(()-> instanceIdentityFrom, ()->instanceIdentityTo);
    }

    public static RelationIdentityImpl identity(Instance.InstanceIdentity instanceIdentityFrom, Instance.InstanceIdentity instanceIdentityTo, RelationClass.RelationClassIdentity relationClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        return new RelationIdentityImpl(instanceIdentityFrom, instanceIdentityTo, relationClassIdentity, serverId, timestamp, addressId);
    }

    public RelationIdentityImpl(Instance.InstanceIdentity instanceIdentityFrom, Instance.InstanceIdentity instanceIdentityTo, RelationClass.RelationClassIdentity relationClassIdentity, Server.ServerId serverId, long timestamp, long addressId) {
        super(serverId, timestamp, addressId);
        this.instanceIdentityFrom = instanceIdentityFrom;
        this.instanceIdentityTo = instanceIdentityTo;
        this.relationClassIdentity = relationClassIdentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationIdentityImpl)) return false;
        if (!super.equals(o)) return false;
        RelationIdentityImpl that = (RelationIdentityImpl) o;
        return Objects.equals(getRelationClassIdentity(), that.getRelationClassIdentity()) &&
                Objects.equals(instanceIdentityFrom, that.instanceIdentityFrom) &&
                Objects.equals(instanceIdentityTo, that.instanceIdentityTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRelationClassIdentity(), instanceIdentityFrom, instanceIdentityTo);
    }

    @Override
    public String toString() {
        return "RelationIdentityImpl{" +
                "relationClassIdentity=" + relationClassIdentity +
                ", instanceIdentityFrom=" + instanceIdentityFrom +
                ", instanceIdentityTo=" + instanceIdentityTo +
                "} " + super.toString();
    }
}
