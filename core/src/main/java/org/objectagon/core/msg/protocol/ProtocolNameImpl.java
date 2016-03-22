package org.objectagon.core.msg.protocol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.objectagon.core.msg.Protocol;

/**
 * Created by christian on 2015-10-08.
 */
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Getter
public class ProtocolNameImpl implements Protocol.ProtocolName {
    private String name;

}
