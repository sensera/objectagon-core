package org.objectagon.core.storage.standard;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.storage.Data;
import org.objectagon.core.storage.data.AbstractData;

import java.util.*;

/**
 * Created by christian on 2015-11-01.
 */
public class StandardData extends AbstractData<StandardIdentity,StandardVersion> {

    public static StandardData create(StandardIdentity identity, StandardVersion version, Type dataType) { return new StandardData(identity, version, dataType, new ArrayList());}

    private List<Message.Value> values = new LinkedList<>();
    private Type dataType;

    private StandardData(StandardIdentity identity, StandardVersion version, Type dataType, Iterable<Message.Value> values) {
        super(identity, version);
        this.dataType = dataType;
        values.forEach(this.values::add);
    }

    @Override public Type getDataType() {return dataType;}
    public List<Message.Value> getValues() {return values;}
    @Override public <C extends Data.Change<StandardIdentity, StandardVersion>> C change() {return (C) new Change(this);}

    public static class Change implements Data.Change<StandardIdentity, StandardVersion> {
        private StandardData original;
        private Map<Message.Field,Message.Value> values = new HashMap<>();

        public Change(StandardData original) {
            this.original = original;
            this.original.getValues().forEach(value -> values.put(value.getField(), value));
        }

        public Change setValue(Message.Value value) {
            values.put(value.getField(), value);
            return this;
        }

        @Override
        public <D extends Data<StandardIdentity, StandardVersion>> D create(StandardVersion version) {
            return (D) new StandardData(
                    original.getIdentity(),
                    original.getVersion(),
                    original.getDataType(),
                    values.values());
        }
    }

    @Override
    public Iterable<Message.Value> values() {
        //TODO Fix
        return Arrays.asList(
                MessageValue.name(StandardField.NAME, dataType)
        );
    }
}
