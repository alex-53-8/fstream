package io.alex538.fstream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * An interface which each serialized used with FStream should implement
 * */
public interface FSerializer {

    /**
     * Serializes an instance of a serializable class into bytes and writes a result of
     * serialization into output stream.
     * @param os OutputStream write there a result of serialization
     * @param instance an instance of a class to be serialized
     * @see FJdkSerializer as an example
     * */
    void serialize(OutputStream os, Serializable instance) throws IOException;

    /**
     * Deserializes an instance of a class represented as a bytes' array
     * @param is InputStream read to obtains all bytes of a serialized object
     * @return Object a result of deserialization
     * */
    Object deserialize(InputStream is) throws IOException, ClassNotFoundException;

}
