package com.khmelyuk.memory.vm.storage;

import com.khmelyuk.memory.OutOfBoundException;

import java.nio.ByteBuffer;

/**
 * A byte array storage.
 *
 * @author Ruslan Khmelyuk
 */
public final class ByteBufferStorage implements Storage {

    private ByteBuffer data;
    private int size;

    public ByteBufferStorage(ByteBuffer buffer) {
        this.data = buffer;
        this.size = buffer.capacity();
    }

    public int size() {
        return size;
    }

    @Override
    public void free() {
        data = ByteBuffer.allocate(0);
        size = 0;
    }

    public void write(byte[] data) throws OutOfBoundException {
        if (data.length > this.size) {
            throw new OutOfBoundException();
        }

        this.data.slice().put(data);
    }

    public void write(byte[] data, int offset) throws OutOfBoundException {
        if (offset >= size || data.length + offset > size) {
            throw new OutOfBoundException();
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.put(data);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        if (offset >= size || length + offset > size) {
            throw new OutOfBoundException();
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.put(data, 0, length);
    }

    @Override
    public void write(byte[] data, int offset, int dataOffset, int length) throws OutOfBoundException {
        if (offset >= size || length + offset > size) {
            throw new OutOfBoundException();
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.put(data, dataOffset, length);
    }

    public int read(byte[] data) throws OutOfBoundException {
        int length = data.length;
        if (length > size) {
            length = size;
        }

        this.data.get(data, 0, length);

        return length;
    }

    public int read(byte[] data, int offset, int length) {
        if (offset + length > size) {
            length = size - offset;
        }
        if (length == 0) {
            return -1;
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.get(data, 0, length);

        return length;
    }

    @Override
    public int read(byte[] data, int offset, int dataOffset, int length) {
        if (offset + length > size) {
            length = size - offset;
        }
        if (length == 0) {
            return -1;
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.get(data, dataOffset, length);

        return length;
    }

    public void write(byte data, int offset) throws OutOfBoundException {
        if (offset >= size) {
            throw new OutOfBoundException();
        }

        this.data.put(offset, data);
    }

    public byte read(int offset) {
        if (offset >= this.data.capacity()) {
            return -1;
        }

        return data.get(offset);
    }
}
