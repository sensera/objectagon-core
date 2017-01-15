package org.objectagon.core.object;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Entity;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.data.DataType;
import org.objectagon.core.storage.entity.EntityName;
import org.objectagon.core.storage.standard.StandardVersion;
import org.objectagon.core.task.Task;

import java.util.List;

/**
 * Created by christian on 2016-05-29.
 */
public interface Meta extends Entity<Meta.MetaIdentity, Meta.MetaData> {

    EntityName ENTITY_NAME = EntityName.create("META");

    Message.Field META_IDENTITY = NamedField.address("metaId");
    Message.Field META_NAME = NamedField.name("MetaName");

    Data.Type DATA_TYPE = DataType.create("META");

    enum NameTask implements Task.TaskName {
        NAME_SEARCH
    }

    interface MetaIdentity extends Identity {}
    interface MetaName extends Name {}

    interface MetaData extends Data<MetaIdentity, StandardVersion> {
        MetaName getName();
        List<Method.MethodIdentity> getMethods();
    }

    interface ChangeMeta extends Data.Change<MetaIdentity,StandardVersion> {
        ChangeMeta setName(MetaName metaName);
        ChangeMeta addMethod(Method.MethodIdentity methodIdentity);
        ChangeMeta removeMethod(Method.MethodIdentity methodIdentity);
    }
}
