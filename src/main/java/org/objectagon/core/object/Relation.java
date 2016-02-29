package org.objectagon.core.object;

import org.objectagon.core.msg.Name;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2015-10-18.
 */
public interface Relation extends Entity<Relation.RelationIdentity, Relation.RelationData> {

    interface RelationName extends Name {}

    enum RelationType {
        INHERITANCE,
        AGGREGATE,
        ASSOCIATION,
    }

    enum RelationDirection {
        OWNER,
        TARGET
    }

    interface RelationIdentity extends Identity {}

    interface RelationData extends Data<RelationIdentity, StandardVersion> {}
}
