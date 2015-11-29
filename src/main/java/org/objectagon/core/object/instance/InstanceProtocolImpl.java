package org.objectagon.core.object.instance;

import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.composer.StandardComposer;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.object.FieldAddress;
import org.objectagon.core.object.InstanceProtocol;

import static org.objectagon.core.msg.message.VolatileAddressValue.address;
import static org.objectagon.core.msg.message.VolatileTextValue.text;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceProtocolImpl extends AbstractProtocol<InstanceProtocol.Session> {

    public InstanceProtocolImpl(ProtocolName name) {
        super(name);
    }

    @Override
    public InstanceProtocol.Session createSession(Transporter transporter, Composer composer, SessionOwner sessionOwner) {
        return new InstanceProtocolSession(nextSessionId(), composer, transporter, sessionOwner);
    }


    class InstanceProtocolSession extends AbstractProtocolSession implements InstanceProtocol.Session {

        public InstanceProtocolSession(SessionId sessionId, Composer composer, Transporter transporter, SessionOwner sessionOwner) {
            super(sessionId, composer, transporter, sessionOwner);
        }

        public void getValue(FieldAddress fieldAddress) {
            send(InstanceProtocol.MessageName.GET_VALUE, address(InstanceProtocol.FieldName.FIELD, fieldAddress));
        }

        public void setValue(FieldAddress fieldAddress, String value) {
            send(InstanceProtocol.MessageName.SET_VALUE, address(InstanceProtocol.FieldName.FIELD, fieldAddress), text(InstanceProtocol.FieldName.VALUE, value));
        }

    }

}
