package io.alex538.fstream;

import java.nio.ByteBuffer;

final class FBytes {

    private FBytes(){}

    static void assignFromTheRight(byte[] target, byte[] source) {
        if (source.length > target.length) {
            throw new RuntimeException(
                    String.format("Cannot set bytes as target [%s] is shorter than source [%s]", target.length, source.length)
            );
        }

        for(int s = source.length - 1, t = target.length - 1; s >= 0; s--, t--) {
            target[t] = source[s];
        }
    }

    static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

}
