package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.field.StandardField;

/**
 * Created by christian on 2015-10-11.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class NamedAddress implements Address, Name {
    Name name;

    @Override
    public void toValue(Message.BuilderItem builderItem) {
        builderItem.create(StandardField.NAME).set(name);
    }
}
