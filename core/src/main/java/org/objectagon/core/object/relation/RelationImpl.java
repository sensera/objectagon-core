package org.objectagon.core.object.relation;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationProtocol;
import org.objectagon.core.object.relation.data.RelationDataImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-03-07.
 */
public class RelationImpl extends EntityImpl<Relation.RelationIdentity, Relation.RelationData, StandardVersion, EntityWorker> implements Relation {

    public RelationImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected RelationData upgrade(RelationData data, DataVersion<RelationIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data;
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected RelationData createNewData() {
        return RelationDataImpl.create(getAddress(), StandardVersion.create(0L));
    }

    @Override
    protected EntityWorker createWorker(Receiver.WorkerContext workerContext) {
        return new EntityWorkerImpl(workerContext);
    }

    @Override
    protected RelationIdentity createAddress(Server.ServerId serverId, long timestamp, long id, Receiver.Initializer<RelationIdentity> initializer) {
        RelationProtocol.ConfigRelation configRelation = (RelationProtocol.ConfigRelation) initializer.<RelationProtocol.ConfigRelation>initialize(getAddress());
        return new RelationIdentityImpl(
                configRelation.getInstanceIdentity(),
                configRelation.getRelationClassIdentity(),
                serverId,
                timestamp,
                id
        );
    }
}
