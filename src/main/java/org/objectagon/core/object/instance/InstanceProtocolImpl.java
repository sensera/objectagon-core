package org.objectagon.core.object.instance;

import org.objectagon.core.msg.*;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.object.FieldAddress;
import org.objectagon.core.object.InstanceProtocol;

/**
 * Created by christian on 2015-10-20.
 */
public class InstanceProtocolImpl extends AbstractProtocol<InstanceProtocol.Session> {

    public InstanceProtocolImpl(ProtocolName name, Composer composer, Transporter transporter) {
        super(name, composer, transporter);
    }

    public InstanceProtocolSession createSession(Address target) {
        return new InstanceProtocolSession(new Composer() {
            public Envelope create(Message message) {
                return null;
            }

            public Envelope create(Message.MessageName messageName) {
                return null;
            }

            public Builder builder(Message message) {
                return null;
            }
        }, target);
    }

    public InstanceProtocolSession createSession(Composer composer, Address target) {
        return new InstanceProtocolSession(composer, target);
    }

    class InstanceProtocolSession extends AbstractProtocolSession implements InstanceProtocol.Session {

        public InstanceProtocolSession(Composer composer, Address target) {
            super(composer, target);
        }

        public void getValue(FieldAddress fieldAddress) {

        }

        public void setValue(FieldAddress fieldAddress, String value) {

        }
    }

}
