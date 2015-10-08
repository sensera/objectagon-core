package org.objectagon.core.msg;

/**
 * Created by christian on 2015-10-07.
 */
public interface Transporter {
    void transport(Envelope envelope);
}
