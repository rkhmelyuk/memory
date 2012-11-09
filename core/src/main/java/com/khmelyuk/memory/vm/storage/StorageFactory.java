package com.khmelyuk.memory.vm.storage;

import com.khmelyuk.memory.MemoryException;

/**
 * A factory for storage.
 *
 * @author Ruslan Khmelyuk
 */
public interface StorageFactory {

    /**
     * Creates a storage of specified size.
     *
     * @param begin the begin index of storage.
     * @param size  the storage size.
     * @return the created storage.
     * @throws MemoryException error to create a new storage.
     */
    Storage create(int begin, int size) throws MemoryException;
}
