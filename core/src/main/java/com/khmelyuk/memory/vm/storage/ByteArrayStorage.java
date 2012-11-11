package com.khmelyuk.memory.vm.storage;

import com.khmelyuk.memory.OutOfBoundException;

/**
 * A byte array storage.
 *
 * @author Ruslan Khmelyuk
 */
public final class ByteArrayStorage implements Storage {

    private byte[] data;
    private int size;

    public ByteArrayStorage(int size) {
        this.size = size;
        this.data = new byte[size];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void free() {
        data = new byte[0];
        size = 0;
    }

    @Override
    public void write(byte[] data) throws OutOfBoundException {
        if (data.length > this.data.length) {
            throw new OutOfBoundException();
        }

        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    @Override
    public void write(byte[] data, int offset) throws OutOfBoundException {
        if (offset >= size || data.length + offset > size) {
            throw new OutOfBoundException();
        }

        System.arraycopy(data, 0, this.data, offset, data.length);
    }

    @Override
    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        if (offset >= size || length + offset > size) {
            throw new OutOfBoundException();
        }

        System.arraycopy(data, 0, this.data, offset, length);
    }

    @Override
    public void write(byte[] data, int offset, int dataOffset, int length) throws OutOfBoundException {
        if (offset >= size || length + offset > size) {
            throw new OutOfBoundException();
        }

        System.arraycopy(data, dataOffset, this.data, offset, length);
    }

    @Override
    public int read(byte[] data) {
        int length = data.length;
        if (length > size) {
            length = size;
        }

        System.arraycopy(this.data, 0, data, 0, length);

        return length;
    }

    @Override
    public int read(byte[] data, int offset, int length) {
        if (data.length < length) {
            length = data.length;
        }
        if (length == 0 || offset + length > size) {
            return -1;
        }

        System.arraycopy(this.data, offset, data, 0, length);

        return length;
    }

    @Override
    public int read(byte[] data, int offset, int dataOffset, int length) {
        if (data.length < length) {
            length = data.length;
        }
        if (length == 0 || offset + length > size) {
            return -1;
        }

        System.arraycopy(this.data, offset, data, dataOffset, length);

        return length;
    }

    @Override
    public void write(byte data, int offset) throws OutOfBoundException {
        if (offset >= size) {
            throw new OutOfBoundException();
        }
        this.data[offset] = data;
    }

    @Override
    public byte read(int offset) {
        if (offset >= size) {
            return -1;
        }
        return this.data[offset];
    }
}
