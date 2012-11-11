package com.khmelyuk.memory;

import com.khmelyuk.memory.vm.FixedVirtualMemory;
import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

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
        assert size >= 0 : "Memory size can't negative";

        return new Memory(new FixedVirtualMemory(
                new ByteArrayStorage(size),
                new LinkedVirtualMemoryTable(size)));
    }

    /**
     * Allocated a memory with specified size.
     *
     * @param size the memory size.
     * @return the memory with specified size.
     * @see #allocate(int)
     */
    public Memory allocate(MemorySize size) {
        return allocate(size.getBytes());
    }

}
