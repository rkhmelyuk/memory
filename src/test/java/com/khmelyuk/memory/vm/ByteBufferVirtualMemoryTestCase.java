package com.khmelyuk.memory.vm;

import org.junit.Assert;
import org.junit.Test;

import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

import java.nio.ByteBuffer;

/**
 * @author Ruslan Khmelyuk
 */
public class ByteBufferVirtualMemoryTestCase {

    @Test
    public void testLength() {
        VirtualMemory memory = createVirtualMemory(100);
        Assert.assertEquals(100, memory.size());

        memory = createVirtualMemory(0);
        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void testGetBlock() {
        VirtualMemory memory = createVirtualMemory(100);
        VirtualMemoryBlock block = memory.allocate(100);

        Assert.assertNotNull(block);
        Assert.assertEquals(100, block.size());
    }

    @Test
    public void testGetFreeUsedSize_Full() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.allocate(100);

        Assert.assertEquals(0, memory.getFreeSize());
        Assert.assertEquals(100, memory.getUsedSize());
    }

    @Test
    public void testGetFreeUsedSize_Half() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.allocate(50);

        Assert.assertEquals(50, memory.getFreeSize());
        Assert.assertEquals(50, memory.getUsedSize());
    }

    @Test
    public void testGetFreeUsedSize_Empty() {
        VirtualMemory memory = createVirtualMemory(100);

        Assert.assertEquals(100, memory.getFreeSize());
        Assert.assertEquals(0, memory.getUsedSize());
    }

    @Test
    public void testGetFreeUsedSize_AfterFree() {
        VirtualMemory memory = createVirtualMemory(100);
        VirtualMemoryBlock block = memory.allocate(50);

        Assert.assertEquals(50, memory.getFreeSize());
        Assert.assertEquals(50, memory.getUsedSize());

        memory.free(block);

        Assert.assertEquals(100, memory.getFreeSize());
        Assert.assertEquals(0, memory.getUsedSize());
    }

    private VirtualMemory createVirtualMemory(int size) {
        return new ByteBufferVirtualMemory(
                ByteBuffer.allocate(size),
                new LinkedVirtualMemoryTable(size));
    }

}
