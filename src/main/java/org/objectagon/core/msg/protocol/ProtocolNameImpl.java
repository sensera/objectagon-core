package org.objectagon.core.msg.protocol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.objectagon.core.Server;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;

import java.util.Objects;

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
