package com.khmelyuk.memory;

import com.khmelyuk.memory.space.FreeSpaceListener;
import com.khmelyuk.memory.space.MemorySpace;
import com.khmelyuk.memory.space.Space;
import com.khmelyuk.memory.vm.VirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;

/**
 * Represents a memory block.
 *
 * @author Ruslan Khmelyuk
 */
public class Memory {

    public static final int KB = 1024;
    public static final int MB = KB * KB;
    public static final int GB = MB * MB;

    private final VirtualMemory vm;
    private final FreeSpaceListener freeSpaceListener;

    public Memory(VirtualMemory vm) {
        this.vm = vm;

        freeSpaceListener = new FreeSpaceListener() {
            public void onFreeSpace(Space space) {
                freeSpace(space);
            }
        };
    }

    /**
     * Allocates a memory space of specified size.
     *
     * @param length the memory size.
     * @return the new space
     * @throws OutOfMemoryException error to allocate a memory.
     */
    public MemorySpace allocate(int length) throws OutOfMemoryException {
        final VirtualMemoryBlock block = vm.allocate(length);
        final MemorySpace space = new MemorySpace(this, block);
        space.setFreeSpaceListener(freeSpaceListener);

        return space;
    }

    /**
     * Frees the memory.
     */
    public void free() {
        vm.free();
    }

    /**
     * Free the space.
     *
     * @param space the space to free.
     */
    private void freeSpace(Space space) {
        vm.free(space.getBlock());
    }

    /**
     * Gets the memory size.
     *
     * @return the memory size.
     */
    public int size() {
        return vm.size();
    }

    /**
     * Gets the size of free memory.
     *
     * @return the size of free memory.
     */
    public int getFreeMemorySize() {
        return vm.getFreeSize();
    }

    /**
     * Gets the size of used memory.
     *
     * @return the size of used memory.
     */
    public int getUsedMemorySize() {
        return vm.getUsedSize();
    }
}
