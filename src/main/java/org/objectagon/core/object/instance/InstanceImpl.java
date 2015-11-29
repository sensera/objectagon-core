package org.objectagon.core.object.instance;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.receiver.BasicWorker;
import org.objectagon.core.object.*;
import org.objectagon.core.storage.EntityProtocol;
import org.objectagon.core.storage.entity.EntityImpl;
import org.objectagon.core.storage.entity.EntityWorker;
import org.objectagon.core.storage.entity.EntityWorkerImpl;
import org.objectagon.core.task.StandardTask;
import org.objectagon.core.task.Task;
import org.objectagon.core.utils.Util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceImpl extends EntityImpl<InstanceIdentity,InstanceData,ObjectVersion,InstanceImpl.InstanceWorker, InstanceImpl> {

    public InstanceImpl(EntityCtrl<InstanceIdentity, InstanceImpl> entityCtrl, InstanceData data) {
        super(entityCtrl, data);

    }

    protected void requestValue(InstanceWorker worker, Task.TaskName taskName, StandardTask.SendMessageAction<FieldValueProtocol.Session> action, Task.SuccessAction successAction) {
        FieldAddress fieldAddress = worker.getValue(InstanceProtocol.FieldName.FIELD).asAddress();

        try {
            InstanceData instanceData = getDataByVersion(worker.getVersion());

            FieldValueAddress fieldValueAddress = instanceData.getValueByField(fieldAddress);

            worker.getTaskBuilder().message(
                    taskName,
                    FieldValueProtocol.FIELD_VALUE_PROTOCOL,
                    fieldValueAddress,
                    action
            )
                    .success(successAction)
                    .start();

        } catch (UserException e) {
            if (e.getErrorKind().equals(ErrorKind.FIELD_NOT_FOUND)) {
                worker.replyWithError(ErrorKind.FIELD_NOT_FOUND);
                return;
            }
        }
    }

    private void getValue(InstanceWorker worker) {
        requestValue(
                worker,
                Instance.TaskName.GET_VALUE,
                FieldValueProtocol.Session::getValue,
                worker.replyWithSelectedFieldFromResult(FieldValueProtocol.FieldName.VALUE, ErrorClass.INSTANCE)
        );
    }

    private void setValue(InstanceWorker worker) {
        requestValue(
                worker,
                Instance.TaskName.SET_VALUE,
                session -> session.setValue(worker.getValue(InstanceProtocol.FieldName.VALUE)),
                worker.replyWithSelectedFieldFromResult(FieldValueProtocol.FieldName.VALUE, ErrorClass.INSTANCE)
        );
    }

    @Override
    protected void handle(InstanceWorker worker) {
        triggerBuilder(worker)
                .trigger(InstanceProtocol.MessageName.GET_VALUE, this::getValue)
                .trigger(InstanceProtocol.MessageName.SET_VALUE, this::setValue)
                .orElse(w -> super.handle(w));
    }

    @Override
    protected InstanceWorker createWorker(WorkerContext workerContext) {
        return new InstanceWorker(workerContext);
    }

    @Override
    protected ObjectVersion createVersionFromValue(Message.Value value) {
        return new ObjectVersion(value.asNumber());
    }

    public static class InstanceWorker extends EntityWorkerImpl {
        public InstanceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }
}
