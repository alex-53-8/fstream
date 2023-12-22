package io.alex538.fstream;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FInputStreamTest {

    @Test
    public void test() throws IOException {
        RandomAccessFile raf = mock(RandomAccessFile.class);
        int[] response = new int[]{0};
        when(raf.read()).thenAnswer(a -> response[0]++);

        FInputStream is = new FInputStream(raf, 0, 3);

        assertEquals(0, is.read());
        assertEquals(1, is.read());
        assertEquals(2, is.read());
        assertEquals(-1, is.read());

        verify(raf, times(3)).read();
        verify(raf, times(1)).seek(0);
        verify(raf, times(1)).seek(1);
        verify(raf, times(1)).seek(2);
        verifyNoMoreInteractions(raf);
    }

}