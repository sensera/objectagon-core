package feature.utils;

import java.io.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-01-22.
 */
public class ReadStream {

    public static byte[] readStream(InputStream inputStream) throws IOException {
        if (inputStream == null)
            throw new IOException("No stream");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    public static Function<Reader, String> createStringFromReader = reader ->
            new BufferedReader(reader)
                    .lines()
                    .collect(Collectors.joining("\n"));

}
