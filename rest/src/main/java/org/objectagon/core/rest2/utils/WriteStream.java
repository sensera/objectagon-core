package org.objectagon.core.rest2.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by christian on 2017-01-22.
 */
public class WriteStream {

    public static void write(OutputStream out, String content) throws IOException {
        out.write(content.getBytes());
        out.flush();
    }
}
