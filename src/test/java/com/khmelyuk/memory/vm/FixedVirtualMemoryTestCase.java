package com.khmelyuk.memory.vm;

import org.junit.Assert;
import org.junit.Test;

import com.khmelyuk.memory.vm.block.VirtualMemoryBlock;

/**
 * @author Ruslan Khmelyuk
 */
public class FixedVirtualMemoryTestCase {

    @Test
    public void testLength() {
        VirtualMemory memory = new FixedVirtualMemory(new byte[100]);
        Assert.assertEquals(100, memory.length());

        memory = new FixedVirtualMemory(new byte[0]);
        Assert.assertEquals(0, memory.length());
    }

    @Test
    public void testGetBlock() {
        VirtualMemory memory = new FixedVirtualMemory(new byte[100]);
        VirtualMemoryBlock block = memory.getBlock(0, 100);

        Assert.assertNotNull(block);
        Assert.assertEquals(100, block.length());
    }

}
