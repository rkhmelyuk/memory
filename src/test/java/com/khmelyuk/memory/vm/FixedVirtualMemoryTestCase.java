package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

/**
 * @author Ruslan Khmelyuk
 */
public class FixedVirtualMemoryTestCase extends VirtualMemoryTestCase {

    protected VirtualMemory createVirtualMemory(int size) {
        return new FixedVirtualMemory(size, new LinkedVirtualMemoryTable(size));
    }

}
