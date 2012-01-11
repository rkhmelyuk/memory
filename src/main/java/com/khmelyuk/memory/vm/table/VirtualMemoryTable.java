package com.khmelyuk.memory.vm.table;

/**
 * The table of virtual memory blocks.
 *
 * @author Ruslan Khmelyuk
 */
public interface VirtualMemoryTable {

    /**
     * Allocates a block of specified size.
     *
     * @param size the size of block to allocate.
     * @return the allocated block of specified size or null if such block can't be allocated.
     */
    Block allocate(int size);

    /**
     * Frees the block of memory.
     * The input block is always cleared.
     *
     * @param block the block to free.
     * @return true if block was freed, otherwise false.
     */
    boolean free(Block block);

    /**
     * Gets the size of free memory.
     *
     * @return the size of free memory.
     */
    int getFreeMemorySize();

    /**
     * Gets the used memory size.
     *
     * @return the size of the used memory.
     */
    int getUsedMemorySize();

    /**
     * Run memory defragmentation.
     */
    void defragment();

    /**
     * Reset the table for specified size. This will remove any allocations.
     *
     * @param size the new table size.
     */
    void reset(int size);
}
