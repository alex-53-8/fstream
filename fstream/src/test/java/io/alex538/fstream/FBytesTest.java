package io.alex538.fstream;

import org.junit.jupiter.api.Test;

import static io.alex538.fstream.FBytes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FBytesTest {

    @Test
    public void testAssignFromTheRight() {
        byte[] source = {1, 2, 3, 4};
        byte[] dest = {0, 0, 0, 0, 0, 0, 0, 0};

        assignFromTheRight(dest, source);

        assertEquals(1, dest[4]);
        assertEquals(2, dest[5]);
        assertEquals(3, dest[6]);
        assertEquals(4, dest[7]);
    }

    @Test
    public void testAssignFromTheRight_whenSourceIsLongerThanDest_thenExceptionIsThrown() {
        byte[] source = {1, 2, 3, 4};
        byte[] dest = {0};

        assertThrows(RuntimeException.class, () -> assignFromTheRight(dest, source));
    }

    @Test
    public void testToFrom() {
        final long expected = 1234567L;

        byte[] encoded = longToBytes(expected);
        long result = bytesToLong(encoded);

        assertEquals(expected, result);
    }

}