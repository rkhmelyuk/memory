package com.khmelyuk.memory.vm.block;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The none-memory block
 *
 * @author Ruslan Khmelyuk
 */
public final class NoneMemoryBlock implements VirtualMemoryBlock {

    private static NoneMemoryBlock instance = new NoneMemoryBlock();

    public static VirtualMemoryBlock instance() {
        return instance;
    }

    private NoneMemoryBlock() {
    }

    public int getAddress() {
        return -1;
    }

    public int length() {
        return 0;
    }

    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException();
    }

    public void write(byte[] data) {
        throw new UnsupportedOperationException();
    }

    public void write(byte[] data, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public int read(byte[] data) {
        throw new UnsupportedOperationException();
    }

    public int read(byte[] data, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public void write(String string) {
        throw new UnsupportedOperationException();
    }

    public String readString() {
        throw new UnsupportedOperationException();
    }

    public Object readObject() {
        throw new UnsupportedOperationException();
    }

    public void writeObject(Object object) {
        throw new UnsupportedOperationException();
    }
}
