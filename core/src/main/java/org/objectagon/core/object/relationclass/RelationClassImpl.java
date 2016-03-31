package org.objectagon.core.object.relationclass;

import org.objectagon.core.object.Instance;
import org.objectagon.core.object.relationclass.data.RelationClassDataImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.Server;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.object.RelationClass;
import org.objectagon.core.object.RelationClassProtocol;
import org.objectagon.core.object.relation.RelationService;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassImpl extends EntityImpl<RelationClass.RelationClassIdentity, RelationClass.RelationClassData, StandardVersion, EntityWorker> implements RelationClass {

    public RelationClassImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected RelationClassData createNewData() {
        return RelationClassDataImpl.create(getAddress(), StandardVersion.create(0L));
    }

    @Override
    protected RelationClassData upgrade(RelationClassData data, DataVersion<RelationClassIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data;
    }

    @Override
    protected EntityWorker createWorker(WorkerContext workerContext) {
        return new EntityWorkerImpl(workerContext);
    }

    @Override
    protected RelationClassIdentity createAddress(Server.ServerId serverId, long timestamp, long id, Initializer<RelationClassIdentity> initializer) {
        RelationClassProtocol.ConfigRelationClass configInstance = initializer.<RelationClassProtocol.ConfigRelationClass>initialize(getAddress());
        return new RelationClassIdentityImpl(configInstance.getInstanceClassIdentity(), configInstance.getRelationType(), serverId, timestamp, id);
    }

    @Override
    protected void handle(EntityWorker worker) {
        triggerBuilder(worker)
                .trigger(RelationClassProtocol.MessageName.CREATE_RELATION, read(this::createRelation))
                .orElse(w -> super.handle(w));
    }

    private void createRelation(EntityWorker entityWorker, RelationClassData relationClassData) {
        entityWorker.start(
                entityWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, RelationService.NAME).create(
                        entityWorker.getValue(Instance.INSTANCE_IDENTITY),
                        MessageValue.address(getAddress()))
        );
    }
}
