package com.khmelyuk.memory.vm.table;

/**
 * The information about block of memory.
 *
 * @author Ruslan Khmelyuk
 */
public interface Block {

    /**
     * Gets the address block.
     *
     * @return the address block.
     */
    int getAddress();

    /**
     * Gets the block size.
     *
     * @return the block size.
     */
    int getSize();
}
