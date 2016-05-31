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

import java.util.List;
import java.util.Optional;

/**
 * Created by christian on 2016-03-06.
 */
public interface Method extends Entity<Method.MethodIdentity,Method.MethodData> {

    EntityName ENTITY_NAME = EntityName.create("METHOD");

    Message.Field METHOD_NAME = NamedField.name("methodName");
    Message.Field METHOD_IDENTITY = NamedField.address("methodId");
    Message.Field CODE = NamedField.name("code");
    Message.Field PARAMS = NamedField.values("params");
    Message.Field PARAM_NAME  = NamedField.name("paramName");
    Message.Field PARAM_FIELD  = NamedField.name("paramField");
    Message.Field DEFAULT_VALUE = NamedField.values("defaultValue");

    Data.Type DATA_TYPE = DataType.create("METHOD");

    interface MethodIdentity extends Identity {}
    interface MethodName extends Name {}
    interface ParamName extends Name {}

    interface MethodData extends Data<MethodIdentity, StandardVersion> {
        default Type getDataType() {return DATA_TYPE;}
        MethodName getName();
        String getCode();
        List<InvokeParam> getInvokeParams();
    }

    interface InvokeParam {
        ParamName getName();
        Optional<Message.Value> getDefaultValue();
        Message.Field getField();
    }

    interface ChangeMethod extends Data.Change<Method.MethodIdentity,StandardVersion> {
        ChangeMethod setCode(String code);
        ChangeMethod setName(MethodName name);
        ChangeMethod addInvokeParam(ParamName paramName, Message.Field field);
        ChangeMethod addInvokeParam(ParamName paramName, Message.Field field, Message.Value defaultValue);
        ChangeMethod removeInvokeParam(ParamName paramName);
    }

    interface Invoke {
        void invoke(InvokeWorker invokeWorker);
    }

    interface InvokeWorker {
        List<ParamName> getInvokeParams();
        Message.Value getValue(String name);
        Message.Value getValue(ParamName paramName);
        void replyOk();
        void replyOkWithParams(Message.Value... values);
        void failed(ErrorClass errorClass, ErrorKind errorKind, Message.Value... values);

        <T> ValueCreator<T> setValue(String paramName);
        <T> ValueCreator<T> setValue(ParamName paramName);
    }

    interface ValueCreator<T> {
        void set(T value);
    }

}
