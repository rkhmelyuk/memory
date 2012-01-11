package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a virtual memory of fixed size.
 *
 * @author Ruslan Khmelyuk
 */
public class FixedVirtualMemory implements VirtualMemory {

    private byte[] data;
    private VirtualMemoryTable table;

    public FixedVirtualMemory(int length, VirtualMemoryTable table) {
        this.data = new byte[length];
        this.table = table;
    }

    public int size() {
        return data.length;
    }

    public int getFreeSize() {
        return table.getFreeMemorySize();
    }

    public int getUsedSize() {
        return table.getUsedMemorySize();
    }

    @Override
    public VirtualMemoryBlock allocate(int length) throws OutOfMemoryException {
        Block block = table.allocate(length);
        if (block == null) {
            throw new OutOfMemoryException();
        }

        return new VirtualMemoryBlock(this, block);
    }

    @Override
    public void free() {
        data = new byte[0];
        table.reset(0);
    }

    @Override
    public void free(VirtualMemoryBlock block) {
        table.free(block.getBlock());
    }

    // ---------------------------------------------- Read/write support

    public InputStream getInputStream() {
        return new VMInputStream(this);
    }

    public InputStream getInputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size()) {
            throw new OutOfBoundException();
        }

        return new VMInputStream(this, offset, length);
    }

    public OutputStream getOutputStream() {
        return new VMOutputStream(this);
    }

    public OutputStream getOutputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size()) {
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
