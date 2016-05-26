package org.objectagon.core.object.fieldvalue;

import org.objectagon.core.object.Instance;
import org.objectagon.core.object.Relation;
import org.objectagon.core.object.RelationClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by christian on 2016-03-07.
 */
public class RelationUtil {
    public static Predicate<Relation.RelationIdentity> findRelation(RelationClass.RelationClassIdentity relationClassIdentity) {
        if (relationClassIdentity==null)
            throw new NullPointerException("relationClassIdentity is null!");
        return relationIdentity -> Objects.equals(relationIdentity.getRelationClassIdentity(), relationClassIdentity);
    }

    public static Optional<RelationClass.RelationDirection> getDirection(RelationClass.RelationClassIdentity relationClassIdentity, Instance.InstanceIdentity instanceIdentity) {
        if (Objects.equals(relationClassIdentity.getInstanceClassIdentity(RelationClass.RelationDirection.RELATION_TO), instanceIdentity.getInstanceClassIdentity()))
            return Optional.of(RelationClass.RelationDirection.RELATION_TO);
        if (Objects.equals(relationClassIdentity.getInstanceClassIdentity(RelationClass.RelationDirection.RELATION_FROM), instanceIdentity.getInstanceClassIdentity()))
            return Optional.of(RelationClass.RelationDirection.RELATION_FROM);
        return Optional.empty();
    }
}
