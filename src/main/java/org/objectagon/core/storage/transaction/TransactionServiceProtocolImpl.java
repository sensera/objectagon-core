package org.objectagon.core.storage.transaction;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Transporter;
import org.objectagon.core.msg.protocol.AbstractProtocol;
import org.objectagon.core.storage.TransactionServiceProtocol;

/**
 * Created by christian on 2015-10-17.
 */
public class TransactionServiceProtocolImpl extends AbstractProtocol<TransactionServiceProtocol.Session> implements TransactionServiceProtocol {

    public TransactionServiceProtocolImpl(ProtocolName name, Composer composer, Transporter transporter) {
        super(name, composer, transporter);
    }

    public ClientSession createClientSession(Address target) {
        return null;
    }

    public TransactionServiceProtocol.Session createSession(Address target) {
        return null;
    }

    public TransactionServiceProtocol.Session createSession(Composer composer, Address target) {
        return null;
    }
}
