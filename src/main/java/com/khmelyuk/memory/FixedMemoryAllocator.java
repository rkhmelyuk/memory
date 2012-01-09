package com.khmelyuk.memory;

import com.khmelyuk.memory.vm.FixedVirtualMemory;

/**
 * A fixed memory size allocator.
 *
 * @author Ruslan Khmelyuk
 */
public class FixedMemoryAllocator {

    /**
     * Allocated a memory with specified size.
     *
     * @param size the memory size.
     * @return the memory with specified size.
     */
    public Memory allocate(int size) {
        byte[] array = new byte[size];
        return new Memory(new FixedVirtualMemory(array));
    }
}
