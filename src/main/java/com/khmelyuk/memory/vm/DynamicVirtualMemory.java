package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.storage.DynamicStorage;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A virtual memory that supports dynamic growth.
 *
 * @author Ruslan Khmelyuk
 */
public class DynamicVirtualMemory implements VirtualMemory {

    private final Lock resizeLock = new ReentrantLock();

    private DynamicStorage storage;

    private int size;
    private final int maxSize;
    private final int growth;
    private final VirtualMemoryTable table;

    private FreeEventListener freeEventListener;

    public DynamicVirtualMemory(DynamicStorage storage, VirtualMemoryTable table) {
        int growth = storage.getGrowth();
        growth = (growth != 0 ? growth : 1);

        this.table = table;
        this.growth = growth;
        this.storage = storage;
        this.size = storage.size();
        this.maxSize = storage.getMaxSize();
    }

    public int size() {
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

        // if failed to allocate a block,
        // then tries to increase a memory size and allocate.
        while (block == null) {
            if (size >= maxSize) {
                break;
            }

            if (!extendMemorySize()) {
                // if failed to increase memory size - exit this loop
                break;
            }
            block = table.allocate(length);
        }

        if (block == null) {
            // throws exception if failed to allocate the block.
            throw new OutOfMemoryException();
        }

        // returns the allocated VM block.
        return new VirtualMemoryBlock(this, block);
    }

    /**
     * Extends the virtual memory size.
     * This method extends the memory by a {@code growth} step to max {@code maxSize} value.
     *
     * @return true if memory was extended, otherwise false.
     */
    private boolean extendMemorySize() {
        try {
            resizeLock.lock();

            int newSize = Math.min(size + growth, maxSize);

            if (table.canIncreaseSize(newSize)) {
                storage.increaseSize(newSize);
                table.increaseSize(newSize);
                size = newSize;

                return true;
            }

            return false;
        }
        finally {
            resizeLock.unlock();
        }
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

    // ----------------------------------------------------------

    public InputStream getInputStream() {
        return new VMInputStream(this);
    }

    public InputStream getInputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size) {
            throw new OutOfBoundException();
        }

        return new VMInputStream(this, offset, length);
    }

    public OutputStream getOutputStream() {
        return new VMOutputStream(this);
    }

    public OutputStream getOutputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size) {
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
