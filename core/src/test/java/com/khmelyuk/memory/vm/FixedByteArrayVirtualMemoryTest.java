package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

/**
 * @author Ruslan Khmelyuk
 */
public class FixedByteArrayVirtualMemoryTest extends VirtualMemoryTest {

    protected VirtualMemory createVirtualMemory(int size) {
        return new FixedVirtualMemory(
                new ByteArrayStorage(size),
                new LinkedVirtualMemoryTable(size));
    }

}
