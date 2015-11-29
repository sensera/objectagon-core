package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-10-20.
 */
public class ObjectVersion extends StandardVersion implements Version {

    public static ObjectVersion objectVersion(Long version) { return new ObjectVersion(version);}
    public static ObjectVersion objectVersion(Message.Value version) { return new ObjectVersion(version.asNumber());}

    public ObjectVersion(Long version) {
        super(version);
    }
}
