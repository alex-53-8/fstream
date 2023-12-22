package io.alex538.fstream.fst;

import io.alex538.fstream.FSerializer;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;

public class FstSerializer implements FSerializer {

    private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public void serialize(OutputStream writeTo, Serializable instance) throws IOException {
        writeTo.write(conf.asByteArray(instance));
    }

    @Override
    public Object deserialize(InputStream is) throws IOException {
        return conf.asObject(new BufferedInputStream(is).readAllBytes());
    }

}
