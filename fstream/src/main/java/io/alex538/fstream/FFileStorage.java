package io.alex538.fstream;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Iterator;
import java.util.function.Consumer;

import static io.alex538.fstream.FBytes.*;

@Slf4j
class FFileStorage<T extends Serializable> implements AutoCloseable {

    static final int HEADER_SIZE_BYTES = 8;

    private final RandomAccessFile raf;

    private final File storageFile;

    private final FSerializer serializer;

    FFileStorage(FSerializer serializer, String storagePath, String storageName) {
        this(serializer, new File(storagePath), storageName);
    }

    FFileStorage(FSerializer serializer, File storagePath, String storageName) {
        storageFile = new File(storagePath, storageName).getAbsoluteFile();
        this.serializer = serializer;
        try {
            if (!storageFile.exists()) {
                if (!storageFile.createNewFile()) {
                    throw new RuntimeException(String.format("Cannot create storage [%s]", storageFile));
                }
            } else {
                throw new RuntimeException(String.format("Storage exists [%s]", storageFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("An exception during storage creation at path [%s]", storageFile), e);
        }

        try {
            raf = new RandomAccessFile(storageFile, "rwd");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("Storage not found at path [%s]", storageFile));
        }

        log.trace("storage created {}", storageFile);
    }

    File getStoragePath() {
        return storageFile.getParentFile();
    }

    FSerializer getSerializer() {
        return serializer;
    }

    void write(T o) {
        try {
            long beginning = raf.length();
            byte[] header = new byte[HEADER_SIZE_BYTES];
            raf.write(header);

            FOutputStream out = new FOutputStream(raf);
            serializer.serialize(out, o);
            out.close();

            long endOfBlock = raf.length();
            assignFromTheRight(header, longToBytes(endOfBlock));

            raf.seek(beginning);
            raf.write(header);
            raf.seek(endOfBlock);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write an object", e);
        }
    }

    public void close() {
        log.trace("deleting storage [{}]", storageFile);
        try {
            raf.close();
            if (storageFile.exists()) {
                if (!storageFile.delete()) {
                    throw new RuntimeException(String.format("Unable to delete storage [%s]", storageFile));
                } else {
                    log.trace("storage deleted [{}]", storageFile);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void forEach(Consumer<T> consumer) {
        Iterator<T> i = iterator();

        while (i.hasNext()) {
            consumer.accept(i.next());
        }
    }

    Iterator<T> iterator() {
        return new FFileIterator<>(serializer, raf);
    }

    public String toString() {
        return String.format("FFileStorage {storageFile: [%s]}", storageFile);
    }

}

class FFileIterator<T> implements Iterator<T> {

    private final RandomAccessFile raf;

    private final FSerializer serializer;

    private long pos = 0;

    FFileIterator(FSerializer serializer, RandomAccessFile raf) {
        this.serializer = serializer;
        this.raf = raf;
    }

    long position() {
        return pos;
    }

    @Override
    public boolean hasNext() {
        try {
            return pos < raf.length();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public T next() {
        try {
            byte[] header = new byte[FFileStorage.HEADER_SIZE_BYTES];
            raf.seek(pos);
            int bytesRead = raf.read(header, 0, header.length);

            if (bytesRead != header.length) {
                throw new RuntimeException(String.format("Header is corrupted, bytes read %s", bytesRead));
            }

            long nextBlockPos = bytesToLong(header);
            pos += FFileStorage.HEADER_SIZE_BYTES;

            FInputStream in = new FInputStream(raf, pos, nextBlockPos);
            T o = (T) serializer.deserialize(in);
            in.close();

            pos = nextBlockPos;

            return o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class FInputStream extends InputStream {

    private final RandomAccessFile raf;

    private final long endOfBlock;

    private long pos;

    FInputStream(RandomAccessFile raf, long fromInclusively, long toExclusively) {
        this.raf = raf;
        pos = fromInclusively;
        endOfBlock = toExclusively;
    }

    @Override
    public int read() throws IOException {
        if (pos == endOfBlock) {
            return -1;
        }

        raf.seek(pos++);
        return raf.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        raf.seek(pos);

        int remaining = (int) (endOfBlock - pos);
        if (remaining <= 0) {
            return -1;
        }

        int readCount = raf.read(b, off, Math.min(remaining, len));
        pos += readCount;

        return readCount;
    }

}

class FOutputStream extends OutputStream {

    private final RandomAccessFile raf;

    FOutputStream(RandomAccessFile raf) {
        this.raf = raf;
    }

    @Override
    public void write(int data) throws IOException {
        raf.write(data);
    }

    @Override
    public void write(byte[] data) throws IOException {
        raf.write(data);
    }

    @Override
    public void write(byte[] data, int off, int len) throws IOException {
        raf.write(data, off, len);
    }

}
