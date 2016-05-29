package org.objectagon.core.object;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-03-06.
 */
public interface Method extends Entity<Method.MethodIdentity,Method.MethodData> {

    EntityName ENTITY_NAME = EntityName.create("METHOD");

    Message.Field METHOD_NAME = NamedField.name("methodName");
    Message.Field CODE = NamedField.name("code");
    Message.Field PARAMS = NamedField.values("params");

    Data.Type DATA_TYPE = DataType.create("METHOD");

    interface MethodIdentity extends Identity {}
    interface MethodName extends Name {}

    interface MethodData extends Data<MethodIdentity, StandardVersion> {
        default Type getDataType() {return DATA_TYPE;}
        MethodName getName();
        String getCode();
    }

    interface ChangeMethod extends Data.Change<Method.MethodIdentity,StandardVersion> {
        ChangeMethod setCode(String code);
        ChangeMethod setName(MethodName name);
    }

    interface Invoke {
        void invoke(InvokeWorker invokeWorker);
    }

    interface InvokeWorker {
        Message.Value getValue(Message.Field field);
        void replyOk();
        void replyOkWithParams(Message.Value... values);
        void failed(ErrorClass errorClass, ErrorKind errorKind, Message.Value... values);
    }

}
