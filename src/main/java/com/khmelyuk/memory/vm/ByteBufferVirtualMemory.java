package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Represents a virtual memory of fixed size.
 *
 * @author Ruslan Khmelyuk
 */
public class ByteBufferVirtualMemory implements VirtualMemory {

    private ByteBuffer data;
    private VirtualMemoryTable table;

    public ByteBufferVirtualMemory(ByteBuffer data, VirtualMemoryTable table) {
        this.data = data;
        this.table = table;
    }

    public int size() {
        return data.capacity();
    }

    public int getFreeSize() {
        return table.getFreeMemorySize();
    }

    public int getUsedSize() {
        return table.getUsedMemorySize();
    }

    @Override
    public VirtualMemoryBlock allocate(int length) throws OutOfMemoryException, OutOfBoundException {

        if (length < 0) {
            throw new OutOfBoundException();
        }

        Block block = table.allocate(length);
        if (block == null) {
            throw new OutOfMemoryException();
        }

        return new VirtualMemoryBlock(this, block);
    }

    @Override
    public void free() {
        data = ByteBuffer.allocate(0);
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
        if (data.length > this.data.capacity()) {
            throw new OutOfBoundException();
        }

        this.data.slice().put(data);
    }

    public void write(byte[] data, int offset) throws OutOfBoundException {
        final int length = this.data.capacity();
        if (offset >= length || data.length + offset > length) {
            throw new OutOfBoundException();
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.put(data);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        final int dataLength = this.data.capacity();
        if (offset >= dataLength || length + offset > dataLength) {
            throw new OutOfBoundException();
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.put(data, 0, length);
    }

    public int read(byte[] data) throws OutOfBoundException {
        int length = data.length;
        if (length > this.data.capacity()) {
            length = this.data.capacity();
        }

        this.data.get(data, 0, length);

        return length;
    }

    public int read(byte[] data, int offset, int length) {
        int dataLen = this.data.capacity();
        if (offset + length > dataLen) {
            length = dataLen - offset;
        }
        if (length == 0) {
            return -1;
        }

        ByteBuffer buf = this.data.slice();
        buf.position(offset);
        buf.get(data, 0, length);

        return length;
    }

    public void write(byte data, int offset) throws OutOfBoundException {
        if (offset >= this.data.capacity()) {
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
