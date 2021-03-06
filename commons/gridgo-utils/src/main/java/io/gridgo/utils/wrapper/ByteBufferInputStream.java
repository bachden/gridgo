package io.gridgo.utils.wrapper;

import java.io.InputStream;
import java.nio.ByteBuffer;

import lombok.Getter;

public class ByteBufferInputStream extends InputStream {

    @Getter
    private ByteBuffer buffer;

    public ByteBufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return buffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int outputOffset, int len) {
        if (!buffer.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buffer.remaining());
        buffer.get(bytes, outputOffset, len);
        return len;
    }
}
