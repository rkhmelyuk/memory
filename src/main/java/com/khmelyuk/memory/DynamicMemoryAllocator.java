package com.khmelyuk.memory;

import com.khmelyuk.memory.vm.DynamicVirtualMemory;
import com.khmelyuk.memory.vm.storage.DynamicStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

/**
 * Allocates a virtual memory that can growth dynamically.
 *
 * @author Ruslan Khmelyuk
 */
public class DynamicMemoryAllocator {

    /**
     * Allocates a memory with specified size, and sets the max size the memory can grow to.
     *
     * @param size           the memory size.
     * @param maxSize        the max size for the memory
     * @param growthStepSize the size of the memory growth step.
     * @return the memory with specified size.
     */
    public Memory allocate(int size, int maxSize, int growthStepSize) {
        assert size >= 0 : "Memory size can't negative";
        assert size <= maxSize : "Memory size can't be larger maxSize";
        assert growthStepSize > 0 : "Growths step size can't be zero or negative";
        assert growthStepSize <= maxSize : "Growths step size can't be larger maxSize";

        if (size == maxSize) {
            return new FixedMemoryAllocator().allocate(size);
        }

        return new Memory(new DynamicVirtualMemory(
                new DynamicStorage(size, maxSize, growthStepSize),
                size, maxSize, growthStepSize,
                new LinkedVirtualMemoryTable(size)));
    }

    /**
     * Allocates a memory with specified size, and sets the max size the memory can grow to.
     *
     * @param size           the memory size.
     * @param maxSize        the max size for the memory
     * @return the memory with specified size.
     */
    public Memory allocate(int size, int maxSize) {
        return allocate(size, maxSize, size);
    }
}
