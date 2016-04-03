package org.objectagon.core.storage.standard;

import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.utils.FindNamedConfiguration;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardEntity<D extends StandardData> extends EntityImpl<StandardIdentity, D, StandardVersion, EntityWorker> {


    public StandardEntity(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
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
    protected D createNewData() {
        return (D) StandardData.create(getAddress(), StandardVersion.create(0L)).create();
    }

    @Override
    protected D upgrade(D data, DataVersion<StandardIdentity, StandardVersion> newDataVersion, Transaction transaction) {
        return data;
    }

    @Override
    protected void handle(EntityWorker worker) {

    }

    @Override
    protected EntityWorker createWorker(WorkerContext workerContext) {return new EntityWorkerImpl(workerContext);}

}
