package com.khmelyuk.memory.vm;

import org.junit.Assert;
import org.junit.Test;

import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

/**
 * @author Ruslan Khmelyuk
 */
public class FixedVirtualMemoryTestCase {

    @Test
    public void testLength() {
        VirtualMemory memory = new FixedVirtualMemory(100, new LinkedVirtualMemoryTable(100));
        Assert.assertEquals(100, memory.size());

        memory = new FixedVirtualMemory(0, new LinkedVirtualMemoryTable(0));
        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void testGetBlock() {
        VirtualMemory memory = new FixedVirtualMemory(100, new LinkedVirtualMemoryTable(100));
        VirtualMemoryBlock block = memory.allocate(100);

        Assert.assertNotNull(block);
        Assert.assertEquals(100, block.size());
    }

    @Test
    public void testGetFreeUsedSize_Full() {
        VirtualMemory memory = new FixedVirtualMemory(100, new LinkedVirtualMemoryTable(100));
        memory.allocate(100);

        Assert.assertEquals(0, memory.getFreeSize());
        Assert.assertEquals(100, memory.getUsedSize());
    }

    @Test
    public void testGetFreeUsedSize_Half() {
        VirtualMemory memory = new FixedVirtualMemory(100, new LinkedVirtualMemoryTable(100));
        memory.allocate(50);

        Assert.assertEquals(50, memory.getFreeSize());
        Assert.assertEquals(50, memory.getUsedSize());
    }

    @Test
    public void testGetFreeUsedSize_Empty() {
        VirtualMemory memory = new FixedVirtualMemory(100, new LinkedVirtualMemoryTable(100));

        Assert.assertEquals(100, memory.getFreeSize());
        Assert.assertEquals(0, memory.getUsedSize());
    }

    @Test
    public void testGetFreeUsedSize_AfterFree() {
        VirtualMemory memory = new FixedVirtualMemory(100, new LinkedVirtualMemoryTable(100));
        VirtualMemoryBlock block = memory.allocate(50);

        Assert.assertEquals(50, memory.getFreeSize());
        Assert.assertEquals(50, memory.getUsedSize());

        memory.free(block);

        Assert.assertEquals(100, memory.getFreeSize());
        Assert.assertEquals(0, memory.getUsedSize());
    }

}
