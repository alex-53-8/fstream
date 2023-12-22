package io.alex538.fstream;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.util.UUID;

import static io.alex538.fstream.FBytes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FFileIteratorTest {

    @Test
    public void testIterating_whenFewItemsAreInFile_thenItemsAreReadOneByOne() throws IOException, URISyntaxException {
        File parent = new File(this.getClass().getResource(".").toURI());
        File file = new File(parent, "test-storage_" + UUID.randomUUID());
        final RandomAccessFile raf = new RandomAccessFile(file, "rwd");

        byte[] header1 = new byte[8];
        byte[] payload1 = longToBytes(1);
        long firstBlockEndsPos = header1.length + payload1.length;
        assignFromTheRight(header1, longToBytes(firstBlockEndsPos));
        raf.write(header1);
        raf.write(payload1);

        byte[] header2 = new byte[8];
        byte[] payload2 = longToBytes(2);
        long secondBlockEndsPos = firstBlockEndsPos + header2.length + payload2.length;
        assignFromTheRight(header2, longToBytes(secondBlockEndsPos));
        raf.write(header2);
        raf.write(payload2);

        assertEquals(32, raf.length());

        FSerializer serializer = new FSerializer() {
            @Override
            public void serialize(OutputStream writeTo, Serializable instance) {
                throw new RuntimeException();
            }

            @Override
            public Object deserialize(InputStream is) throws IOException {
                return bytesToLong(new BufferedInputStream(is).readAllBytes());
            }
        };
        FFileIterator<Long> i = new FFileIterator<>(serializer, raf);

        // read the first item
        Long result1 = i.next();

        assertEquals(1L, result1);
        assertEquals(firstBlockEndsPos, i.position());

        // read the second item
        Long result2 = i.next();

        assertEquals(2L, result2);
        assertEquals(secondBlockEndsPos, i.position());

        // no more items
        assertFalse(i.hasNext());
        assertEquals(secondBlockEndsPos, i.position());
    }

}