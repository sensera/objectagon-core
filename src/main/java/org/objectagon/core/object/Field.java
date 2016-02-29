package org.objectagon.core.object;

import org.objectagon.core.msg.Name;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-10-18.
 */
public interface Field extends Entity<Field.FieldIdentity,Field.FieldData> {

    interface FieldName extends Name {}

    interface FieldType {}

    interface FieldIdentity extends Identity {

    }

    interface FieldData extends Data<FieldIdentity, StandardVersion> {

    }
}
