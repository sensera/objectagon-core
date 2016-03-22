package org.objectagon.core.object.relationclass.data;

import org.objectagon.core.object.RelationClass;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.standard.StandardVersion;

/**
 * Created by christian on 2016-03-16.
 */
public class RelationClassDataChangeImpl implements RelationClass.RelationClassDataChange {

    private RelationClass.RelationClassData relationClassData;

    public RelationClassDataChangeImpl(RelationClass.RelationClassData relationClassData) {
        this.relationClassData = relationClassData;
    }

    @Override
    public <D extends Data<RelationClass.RelationClassIdentity, StandardVersion>> D create(StandardVersion version) {
        return (D)  new RelationClassDataImpl(
                relationClassData.getIdentity(),
                version,
                relationClassData.getInstanceClassIdentity(),
                relationClassData.getRelationType());
    }
}
