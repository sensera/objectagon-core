package org.objectagon.core.object.instance;

import org.objectagon.core.msg.address.AddressList;
import org.objectagon.core.object.InstanceClassAddress;
import org.objectagon.core.object.ValueAddress;
import org.objectagon.core.object.ObjectVersion;
import org.objectagon.core.object.RelationAddress;
import org.objectagon.core.storage.Data;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceData implements Data<InstanceIdentity, ObjectVersion> {

    private InstanceIdentity identity;
    private ObjectVersion version;

    private InstanceClassAddress instanceClassAddress;
    private AddressList<ValueAddress> values = new AddressList<ValueAddress>();
    private AddressList<RelationAddress> relations = new AddressList<RelationAddress>();

    public InstanceIdentity getIdentity() {return identity;}
    public ObjectVersion getVersion() {return version;}

    public InstanceData(InstanceIdentity identity, ObjectVersion version, InstanceClassAddress instanceClassAddress) {
        this.identity = identity;
        this.version = version;
        this.instanceClassAddress = instanceClassAddress;
    }

}
