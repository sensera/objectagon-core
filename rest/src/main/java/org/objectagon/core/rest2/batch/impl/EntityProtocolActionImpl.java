package org.objectagon.core.rest2.batch.impl;

import org.objectagon.core.msg.Composer;
import org.objectagon.core.msg.Name;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.task.ProtocolTask;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by christian on 2017-04-16.
 */
public class EntityProtocolActionImpl extends AbstractProtocolAction<EntityServiceProtocol.Send> implements Actions.EntityProtocolAction {

    private Name name;

    public EntityProtocolActionImpl(Composer.ResolveTarget target) {
        super(EntityServiceProtocol.ENTITY_SERVICE_PROTOCOL, target);
    }

    @Override public void setName(Name name) {
        this.name = name;
    }

    @Override public boolean filterName(Name name) {return Objects.equals(this.name, name);}

    @Override protected Optional<Name> getName() {return Optional.of(name);}

    @Override
    protected ProtocolTask.SendMessageAction<EntityServiceProtocol.Send> getProtocol() {
        return session -> session.create();
    }
}
