package org.objectagon.core.storage.standard;

import org.objectagon.core.msg.Converter;
import org.objectagon.core.msg.Message;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Version;
import org.objectagon.core.storage.data.AbstractData;

import java.util.*;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardData extends AbstractData<Identity,Version>  {

    public static StandardData.Build create(Identity identity, Version version) { return new Build(identity, version);}
    public static StandardData.Build upgrade(StandardData original) { return new Build(original);}

    private List<Message.Value> values = new LinkedList<>();

    private StandardData(Identity identity, Version version, Iterable<Message.Value> values) {
        super(identity, version);
        values.forEach(this.values::add);
    }

    @Override
    public void convert(Converter.FromData<Data<Identity, Version>> fromData) {

    }

    public static class Build {
        private Map<Message.Field,Message.Value> values = new HashMap<>();
        private Identity identity;
        private Version version;

        public Build(StandardData original) {
            this.identity = original.getIdentity();
            this.version = original.getVersion();
            //setValues(original);
            if ((1==1))
                throw new RuntimeException("Not correct implemented!");
        }

        public Build(Identity identity, Version version) {
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
