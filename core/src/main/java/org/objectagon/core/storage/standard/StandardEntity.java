package org.objectagon.core.storage.standard;

import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardEntity<D extends StandardData> extends EntityImpl<StandardIdentity, D, StandardVersion, EntityWorker> {

    public StandardEntity(ReceiverCtrl receiverCtrl, Data.Type dataType) {
        super(receiverCtrl, dataType);
    }

    @Override
    protected StandardIdentity createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardIdentity::standardIdentity
        );
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected D createNewData(Optional<StandardVersion> version) {
        return (D) StandardData.create(getAddress(), version.orElse(StandardVersion.create(0L)), getDataType());
    }

    @Override
    protected D upgrade(D data, DataRevision<StandardIdentity, StandardVersion> newDataRevision, Transaction transaction) {
        return data;
    }

    @Override
    protected void handle(EntityWorker worker) {

    }

    @Override
    protected EntityWorker createWorker(WorkerContext workerContext) {return new EntityWorkerImpl(workerContext);}

}
