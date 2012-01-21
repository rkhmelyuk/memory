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
     * Reset the table for specified size. This will remove any allocations.
     *
     * @param size the new table size.
     */
    void reset(int size);

    /**
     * Changes the table virtual memory size.
     * This will not affect any existing allocations, but only extend the free size.
     *
     * @param size the new size for table.
     */
    void increaseSize(int size);

    /**
     * Check if possible to increase a table size to specified size.
     * @param size the size to increase to.
     * @return true if size can be increased.
     */
    boolean canIncreaseSize(int size);
}
