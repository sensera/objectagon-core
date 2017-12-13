package org.objectagon.core.object.meta;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.MessageValueFieldUtil;
import org.objectagon.core.object.Meta;
import org.objectagon.core.object.MetaProtocol;
import org.objectagon.core.object.Method;
import org.objectagon.core.object.meta.data.MetaDataImpl;
import org.objectagon.core.object.method.MethodService;
import org.objectagon.core.service.name.NameServiceImpl;
import org.objectagon.core.storage.DataRevision;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.Optional;

/**
 * Created by christian on 2016-05-29.
 */
public class MetaImpl extends EntityImpl<Meta.MetaIdentity, Meta.MetaData, StandardVersion, MetaImpl.MetaWorker> implements Meta {


    public MetaImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl, Meta.DATA_TYPE);
    }

    @Override
    protected StandardVersion internalCreateNewVersionForDataVersion(long counterNumber) {
        return StandardVersion.create(counterNumber);
    }

    @Override
    protected MetaData createNewData(Optional<StandardVersion> version) {
        return MetaDataImpl.create(getAddress(), version.orElse(StandardVersion.create(0L)));
    }

    @Override
    protected MetaWorker createWorker(WorkerContext workerContext) {
        return new MetaWorker(workerContext);
    }

    public class MetaWorker extends EntityWorkerImpl {

        public MetaWorker(WorkerContext workerContext) {
            super(workerContext);
        }

    }

    @Override
    protected MetaData upgrade(MetaData data, DataRevision<MetaIdentity, StandardVersion> newDataRevision, Transaction transaction) {
        return data; // TODO fix this
    }

    @Override
    protected MetaIdentity createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(MetaIdentityImpl::new);
    }

    @Override
    protected void handle(MetaWorker worker) {
        triggerBuilder(worker)
                .trigger(MetaProtocol.MessageName.CREATE_METHOD, write(this::<ChangeMeta>setMethod, this::createMethod))
                .orElse(w -> super.handle(w));
    }

    private Optional<Task> createMethod(MetaWorker metaWorker, MetaData metaData) {
        return Optional.of(metaWorker.createEntityServiceProtocolSend(NameServiceImpl.NAME_SERVICE, MethodService.NAME)
                .create(MessageValue.address(Meta.META_IDENTITY, getAddress())));
    }

    private void setMethod(MetaWorker metaWorker, MetaData metaData, ChangeMeta changeMeta, Message.Values preparedValues) {
        final Message.Value methodIdentityValue = MessageValueFieldUtil.create(preparedValues).getValueByField(StandardField.ADDRESS);
        Method.MethodIdentity methodIdentity = methodIdentityValue.asAddress();
        changeMeta.addMethod(methodIdentity);
    }


}
