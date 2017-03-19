package feature.util;

import org.objectagon.core.msg.protocol.StandardProtocol;

/**
 * Created by christian on 2016-03-20.
 */
public enum ResponseMessageName {

    Ok(StandardProtocol.MessageName.OK_MESSAGE);

    private StandardProtocol.MessageName messageName;

    public StandardProtocol.MessageName getMessageName() {
        return messageName;
    }

    ResponseMessageName(StandardProtocol.MessageName messageName) {

        this.messageName = messageName;
    }
}
