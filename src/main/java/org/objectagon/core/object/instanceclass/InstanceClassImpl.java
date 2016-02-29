package org.objectagon.core.object.instanceclass;

import org.objectagon.core.Server;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.msg.receiver.BasicReceiverImpl;
import org.objectagon.core.object.*;
import org.objectagon.core.storage.DataVersion;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-02-28.
 */
public class InstanceClassImpl extends EntityImpl<InstanceClass.InstanceClassIdentity, InstanceClass.InstanceClassData, StandardVersion, InstanceClassImpl.InstanceClassWorker> implements InstanceClass {

    public InstanceClassImpl(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
    }

    @Override
    protected StandardVersion createVersionFromValue(Message.Value value) {
        return StandardVersion.create(value);
    }

    @Override
    protected InstanceClassWorker createWorker(WorkerContext workerContext) {
        return new InstanceClassWorker(workerContext);
    }

    public class InstanceClassWorker extends EntityWorkerImpl {
        public InstanceClassWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }

    @Override
    protected InstanceClassIdentity createAddress(Server.ServerId serverId, long timestamp, long id, Initializer initializer) {
        return new InstanceClassIdentityImpl(serverId, timestamp, id);
    }

    @Override
    protected void handle(InstanceClassWorker worker) {
        triggerBuilder(worker)
                .trigger(InstanceClassProtocol.MessageName.CREATE_INSTANCE, this::createInstance)
                .trigger(InstanceClassProtocol.MessageName.ADD_FIELD, this::addField)
                .trigger(InstanceClassProtocol.MessageName.ADD_RELATION, this::addRelation)
                .orElse(w -> super.handle(w));
    }

    /*        Task addField(Field.FieldName name);
        Task addRelation(Relation.RelationName name, Relation.RelationType type, InstanceClass.InstanceClassIdentity relatedClass);
        Task createInstance(Message.MessageName constructor, Message.Values constructorParams);
*/

    private void createInstance(InstanceClassWorker entityWorker) {
        Message.MessageName constructor = entityWorker.getValue(StandardField.MESSAGE).asMessage();
        Message.Values constructorParams = entityWorker.getValue(StandardField.VALUES).asValues();

        EntityServiceProtocol.Send entityServiceProtocolSend = entityWorker.createEntityServiceProtocolSend();
    }

    private void addField(InstanceClassWorker entityWorker) {
        Field.FieldName fieldName = entityWorker.getValue(StandardField.NAME).asName();

    }

    private void addRelation(InstanceClassWorker entityWorker) {
        Relation.RelationName relationName = entityWorker.getValue(StandardField.NAME).asName();
        Relation.RelationType type = Relation.RelationType.valueOf(entityWorker.getValue(InstanceClassProtocol.FieldName.RELATION_TYPE).asText());
        InstanceClass.InstanceClassIdentity relatedClass = entityWorker.getValue(InstanceClassProtocol.FieldName.RELATED_CLASS).asAddress();

        DataVersion < InstanceClassIdentity, StandardVersion > dataVersion = getDataVersion();
        dataVersion.change()
        InstanceClassData instanceClassData = getDataByVersion()
    }
}
