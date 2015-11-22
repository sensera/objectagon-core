package org.objectagon.core.storage.persitence;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.PersistenceServiceProtocol;

/**
 * Created by christian on 2015-10-18.
 */
public class PersistenceServiceProtocolImpl extends AbstractProtocol<PersistenceServiceProtocol.Session> implements PersistenceServiceProtocol {

    public PersistenceServiceProtocolImpl(Composer composer, Transporter transporter) {
        super(PERSISTENCE_SERVICE_PROTOCOL, composer, transporter);
    }

    public PersistenceServiceProtocol.Session createSession(Address target) {return null;}

    public PersistenceServiceProtocol.Session createSession(Composer composer, Address target) {
        return null;
    }
}
