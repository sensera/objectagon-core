package org.objectagon.core.storage.standard;

import org.objectagon.core.msg.Message;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.data.AbstractData;

import java.util.*;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardData extends AbstractData<StandardIdentity,StandardVersion> {

    public static StandardData.Build create(StandardIdentity identity, StandardVersion version) { return new Build(identity, version);}
    public static StandardData.Build upgrade(StandardData original) { return new Build(original);}

    private List<Message.Value> values = new LinkedList<>();

    private StandardData(StandardIdentity identity, StandardVersion version, Iterable<Message.Value> values) {
        super(identity, version);
        values.forEach(this.values::add);
    }

    @Override
    public <C extends Data.Change<StandardIdentity, StandardVersion>> C change() {
        return null;
    }

    public static class Build {
        private Map<Message.Field,Message.Value> values = new HashMap<>();
        private StandardIdentity identity;
        private StandardVersion version;

        public Build(StandardData original) {
            this.identity = original.getIdentity();
            this.version = original.getVersion();
        }

        public Build(StandardIdentity identity, StandardVersion version) {
            this.identity = identity;
            this.version = version;
        }

        public Build setValues(Message.Values original) {
            original.values().forEach(value -> values.put(value.getField(), value));
            return this;
        }

        public Build setValue(Message.Value value) {
            values.put(value.getField(), value);
            return this;
        }

        public StandardData create() {
            return new StandardData(identity, version, values.values());
        }

    }
}
