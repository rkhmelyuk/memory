package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.vm.block.SimpleVirtualMemoryBlock;
import com.khmelyuk.memory.vm.block.VirtualMemoryBlock;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a virtual memory of fixed size.
 *
 * @author Ruslan Khmelyuk
 */
public class FixedVirtualMemory implements VirtualMemory {

    private byte[] data;

    public FixedVirtualMemory(byte[] data) {
        this.data = data;
    }

    public int length() {
        return data.length;
    }

    public VirtualMemoryBlock getBlock(int address, int length) {
        return new SimpleVirtualMemoryBlock(this, address, length);
    }

    // ---------------------------------------------- Read/write support

    public InputStream getInputStream() {
        return new VMInputStream(this);
    }

    public InputStream getInputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > length()) {
            throw new OutOfBoundException();
        }

        return new VMInputStream(this, offset, length);
    }

    public OutputStream getOutputStream() {
        return new VMOutputStream(this);
    }

    public OutputStream getOutputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > length()) {
            throw new OutOfBoundException();
        }

        return new VMOutputStream(this, offset, length);
    }

    public void write(byte[] data) throws OutOfBoundException {
        if (data.length > this.data.length) {
            throw new OutOfBoundException();
        }

        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public void write(byte[] data, int offset) throws OutOfBoundException {
        if (offset >= this.data.length || data.length + offset > this.data.length) {
            throw new OutOfBoundException();
        }

        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        if (offset >= this.data.length || length + offset > this.data.length) {
            throw new OutOfBoundException();
        }

        System.arraycopy(data, 0, this.data, 0, length);
    }

    public int read(byte[] data) throws OutOfBoundException {
        int length = data.length;
        if (length > this.data.length) {
            length = this.data.length;
        }

        System.arraycopy(this.data, 0, data, 0, length);

        return length;
    }

    public int read(byte[] data, int offset, int length) {
        int dataLen = this.data.length;
        if (offset + length > dataLen) {
            length = dataLen - offset;
        }
        if (length == 0) {
            return -1;
        }

        System.arraycopy(this.data, offset, data, 0, length);

        return length;
    }

    public void write(byte data, int offset) throws OutOfBoundException {
        if (offset >= this.data.length) {
            throw new OutOfBoundException();
        }
        this.data[offset] = data;
    }

    public byte read(int offset) {
        if (offset >= this.data.length) {
            return -1;
        }
        return this.data[offset];
    }
}
