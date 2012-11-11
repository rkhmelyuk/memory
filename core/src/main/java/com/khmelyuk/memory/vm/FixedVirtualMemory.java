package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.storage.Storage;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

/**
 * Represents a virtual memory of fixed size.
 *
 * @author Ruslan Khmelyuk
 */
public class FixedVirtualMemory extends AbstractVirtualMemory<Storage> {

    public FixedVirtualMemory(Storage storage, VirtualMemoryTable table) {
        super(storage, table);
    }

    @Override
    public VirtualMemoryBlock allocate(int length) throws OutOfMemoryException, OutOfBoundException {
        if (length < 0) {
            throw new OutOfBoundException();
        }

        Block block = table.allocate(length);
        if (block == null) {
            throw new OutOfMemoryException();
        }

        return new VirtualMemoryBlock(this, block);
    }

}
