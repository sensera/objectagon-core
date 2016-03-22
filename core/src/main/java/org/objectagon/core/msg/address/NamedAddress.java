package org.objectagon.core.msg.address;

import org.objectagon.core.msg.Name;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectagon.core.msg.Address;

/**
 * Created by christian on 2015-10-11.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class NamedAddress implements Address, Name {
    Name name;
}
