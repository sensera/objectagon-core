package org.objectagon.core.object.instance;

import org.objectagon.core.msg.Envelope;
import org.objectagon.core.storage.entity.EntityImpl;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceImpl extends EntityImpl<InstanceIdentity,InstanceData> {

    public InstanceImpl(EntityCtrl entityCtrl, InstanceData data) {
        super(entityCtrl);
    }

    @Override
    public void receive(Envelope envelope) {
        super.receive(envelope);
    }
}
