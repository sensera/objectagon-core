package org.objectagon.core.service;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.protocol.ProtocolNameImpl;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.message.FieldNameImpl;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2015-10-08.
 */
public interface ServiceProtocol extends Protocol<ServiceProtocol.Send, ServiceProtocol.Reply> {

    ProtocolName SERVICE_PROTOCOL = new ProtocolNameImpl("SERVICE_PROTOCOL");

    enum MessageName implements Message.MessageName, Task.TaskName {
        START_SERVICE,
        STOP_SERVICE,
        FAILED,
    }

    enum FieldName implements Message.Field {
        ERROR_DESCRIPTION("ERROR_SEVERITY", Message.FieldType.Text),
        ERROR_KIND("ERROR_KIND", Message.FieldType.Text);

        private Message.FieldName name;
        private Message.FieldType fieldType;

        public Message.FieldName getName() {return name;}
        public Message.FieldType getFieldType() {return fieldType;}

        @Override
        public boolean sameField(Message.Value value) {
            return equals(value.getField());
        }

        FieldName(String name, Message.FieldType fieldType) {
            this.name = new FieldNameImpl(name);
            this.fieldType = fieldType;
        }
    }

    interface Send extends Protocol.Send {
        Task startService();
        Task stopService();
    }

    interface Reply extends Protocol.Reply {
        void replyWithError(ErrorKind errorKind);
    }

    enum ErrorKind {
        ServiceIsStoppning, ServiceIsStarting;
    }

}
