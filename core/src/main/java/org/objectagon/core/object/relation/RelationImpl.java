package org.objectagon.core.object.relation;

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
import org.objectagon.core.utils.FindNamedConfiguration;

import static org.objectagon.core.storage.entity.EntityService.EXTRA_ADDRESS_CONFIG_NAME;

/**
 * Created by christian on 2016-03-07.
 */
public class RelationImpl extends EntityImpl<Relation.RelationIdentity, Relation.RelationData, StandardVersion, EntityWorker> implements Relation {

    public RelationImpl(Receiver.ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, Relation.DATA_TYPE);
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
    protected RelationIdentity createAddress(Configurations... configurations) {
        FindNamedConfiguration finder = FindNamedConfiguration.finder(configurations);
        RelationProtocol.ConfigRelation configRelation = finder.getConfigurationByName(EXTRA_ADDRESS_CONFIG_NAME);
        return finder.createConfiguredAddress((serverId, timestamp, id) ->
                new RelationIdentityImpl(
                configRelation.getInstanceIdentity(),
                configRelation.getRelationClassIdentity(),
                serverId,
                timestamp,
                id)
        );
    }
}
