package org.objectagon.core.object.instance;

import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.*;
import org.objectagon.core.msg.composer.StandardComposer;
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
        return new InstanceProtocolSession(StandardComposer.create(target));
    }

    @Override
    public InstanceProtocol.Session createSession(Composer composer, Address target) {
        return new InstanceProtocolSession(composer);
    }

    public InstanceProtocolSession createSession(Composer composer) {
        return new InstanceProtocolSession(composer);
    }

    class InstanceProtocolSession extends AbstractProtocolSession implements InstanceProtocol.Session {

        public InstanceProtocolSession(Composer composer) {
            super(composer);
        }

        public void getValue(FieldAddress fieldAddress) {

        }

        public void setValue(FieldAddress fieldAddress, String value) {

        }

        @Override
        public void replyWithError(ErrorKind errorKind) {

        }
    }

}
