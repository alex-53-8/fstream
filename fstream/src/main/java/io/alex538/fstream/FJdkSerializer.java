package io.alex538.fstream;

import java.io.*;

public class FJdkSerializer implements FSerializer {

    @Override
    public void serialize(OutputStream os, Serializable o) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(o);
        oos.close();
    }

    @Override
    public Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readObject();
    }

}
