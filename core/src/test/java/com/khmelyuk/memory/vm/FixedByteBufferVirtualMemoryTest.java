package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.vm.storage.ByteBufferStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

import java.nio.ByteBuffer;

/**
 * @author Ruslan Khmelyuk
 */
public class FixedByteBufferVirtualMemoryTest extends VirtualMemoryTest {

    protected VirtualMemory createVirtualMemory(int size) {
        return new FixedVirtualMemory(
                new ByteBufferStorage(ByteBuffer.allocate(size)),
                new LinkedVirtualMemoryTable(size));
    }

}
