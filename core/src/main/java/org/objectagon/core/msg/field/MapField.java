package org.objectagon.core.msg.field;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.message.FieldNameImpl;

/**
 * Created by christian on 2015-10-17.
 */
public class MapField implements Message.Field {

    public static MapField create(String name) { return new MapField(name); }

    private final Message.FieldName name;

    public Message.FieldName getName() {return name;}
    public Message.FieldType getFieldType() {return Message.FieldType.Map;}

    @Override
    public boolean sameField(Message.Value value) {
        return equals(value.getField());
    }

    private MapField(String name) {
        this.name = new FieldNameImpl(name);
    }
}
