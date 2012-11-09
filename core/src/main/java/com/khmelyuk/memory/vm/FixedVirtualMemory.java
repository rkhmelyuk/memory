package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.storage.Storage;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a virtual memory of fixed size.
 *
 * @author Ruslan Khmelyuk
 */
public class FixedVirtualMemory implements VirtualMemory {

    private Storage storage;
    private VirtualMemoryTable table;
    private int size;

    private FreeEventListener freeEventListener;

    public FixedVirtualMemory(Storage storage, VirtualMemoryTable table) {
        this.table = table;
        this.storage = storage;
        this.size = storage.size();
    }

    public int size() {
        return size;
    }

    public int getMaxSize() {
        return size;
    }

    public int getFreeSize() {
        return table.getFreeMemorySize();
    }

    public int getUsedSize() {
        return table.getUsedMemorySize();
    }

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

    public void free() {
        storage.free();
        table.reset(0);
        size = 0;

        if (freeEventListener != null) {
            freeEventListener.onFree(this);
        }
    }

    public void free(VirtualMemoryBlock block) {
        table.free(block.getBlock());
    }

    public void setFreeEventListener(FreeEventListener listener) {
        this.freeEventListener = listener;
    }

    public VirtualMemoryStatistic getStatistic() {
        VirtualMemoryStatistic statistic = new VirtualMemoryStatistic();
        table.fillStatisticInformation(statistic);
        return statistic;
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
        this.storage.write(data);
    }

    public void write(byte[] data, int offset) throws OutOfBoundException {
        this.storage.write(data, offset);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        this.storage.write(data, offset, length);
    }

    public int read(byte[] data) throws OutOfBoundException {
        return this.storage.read(data);
    }

    public int read(byte[] data, int offset, int length) {
        return this.storage.read(data, offset, length);
    }

    public void write(byte data, int offset) throws OutOfBoundException {
        this.storage.write(data, offset);
    }

    public byte read(int offset) {
        return this.storage.read(offset);
    }
}
