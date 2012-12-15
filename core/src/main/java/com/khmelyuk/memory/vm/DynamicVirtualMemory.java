package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.metrics.TimeContext;
import com.khmelyuk.memory.vm.storage.DynamicStorage;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A virtual memory that supports dynamic growth.
 *
 * @author Ruslan Khmelyuk
 */
public class DynamicVirtualMemory extends AbstractVirtualMemory<DynamicStorage> {

    private final Lock resizeLock = new ReentrantLock();

    private final int maxSize;
    private final int growth;

    public DynamicVirtualMemory(DynamicStorage storage, VirtualMemoryTable table) {
        super(storage, table);
        int growth = storage.getGrowth();
        growth = (growth != 0 ? growth : 1);

        this.growth = growth;
        this.maxSize = storage.getMaxSize();
    }

    @Override
    public VirtualMemoryBlock allocate(int length) throws OutOfMemoryException, OutOfBoundException {
        if (length < 0) {
            throw new OutOfBoundException();
        }

        TimeContext timer = metrics.getTimer("vm.allocationTime");
        timer.start();

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

        timer.stop();

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
        } finally {
            resizeLock.unlock();
        }
    }

}
