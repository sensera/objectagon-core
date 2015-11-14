package org.objectagon.core.domain;

import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.data.AbstractData;

/**
 * Created by christian on 2015-11-01.
 */
public class UserData extends AbstractData implements Data {

    public UserData(Identity identity, Version version) {
        super(identity, version);
    }
}
