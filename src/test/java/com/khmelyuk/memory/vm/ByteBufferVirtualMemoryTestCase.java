package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

import java.nio.ByteBuffer;

/**
 * @author Ruslan Khmelyuk
 */
public class ByteBufferVirtualMemoryTestCase extends VirtualMemoryTestCase {

    protected VirtualMemory createVirtualMemory(int size) {
        return new ByteBufferVirtualMemory(
                ByteBuffer.allocate(size),
                new LinkedVirtualMemoryTable(size));
    }

}
