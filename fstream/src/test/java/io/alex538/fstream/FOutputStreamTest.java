package io.alex538.fstream;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.mockito.Mockito.*;

class FOutputStreamTest {

    @Test
    public void test() throws IOException {
        RandomAccessFile raf = mock(RandomAccessFile.class);
        FOutputStream os = new FOutputStream(raf);

        os.write(1);
        os.write(2);
        os.flush();

        verify(raf, times(1)).write(1);
        verify(raf, times(1)).write(2);
    }

}