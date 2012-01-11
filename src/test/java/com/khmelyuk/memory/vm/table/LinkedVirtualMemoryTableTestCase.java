package com.khmelyuk.memory.vm.table;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class LinkedVirtualMemoryTableTestCase {

    @Test
    public void testAllocateMemory() {
        VirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        Block block = table.allocate(20);

        Assert.assertNotNull(block);
        Assert.assertEquals(0, block.getAddress());
        Assert.assertEquals(20, block.getSize());
        Assert.assertEquals(180, table.getFreeMemorySize());
        Assert.assertEquals(20, table.getUsedMemorySize());

        Block block2 = table.allocate(50);

        Assert.assertNotNull(block2);
        Assert.assertEquals(20, block2.getAddress());
        Assert.assertEquals(50, block2.getSize());
        Assert.assertEquals(130, table.getFreeMemorySize());
        Assert.assertEquals(70, table.getUsedMemorySize());
    }

    @Test
    public void testAllocateEntireMemory() {
        VirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        Block block = table.allocate(200);

        Assert.assertNotNull(block);
        Assert.assertEquals(0, block.getAddress());
        Assert.assertEquals(200, block.getSize());
        Assert.assertEquals(0, table.getFreeMemorySize());
        Assert.assertEquals(200, table.getUsedMemorySize());
    }

    @Test
    public void testFreeMemory() {
        VirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        Block block = table.allocate(20);

        Assert.assertNotNull(block);
        Assert.assertEquals(0, block.getAddress());
        Assert.assertEquals(20, block.getSize());
        Assert.assertEquals(180, table.getFreeMemorySize());
        Assert.assertEquals(20, table.getUsedMemorySize());

        Block block2 = table.allocate(50);

        Assert.assertNotNull(block2);
        Assert.assertEquals(20, block2.getAddress());
        Assert.assertEquals(50, block2.getSize());
        Assert.assertEquals(130, table.getFreeMemorySize());
        Assert.assertEquals(70, table.getUsedMemorySize());

        Assert.assertTrue(table.free(block));
        Assert.assertEquals(150, table.getFreeMemorySize());
        Assert.assertEquals(50, table.getUsedMemorySize());

        Assert.assertTrue(table.free(block2));
        Assert.assertEquals(200, table.getFreeMemorySize());
        Assert.assertEquals(0, table.getUsedMemorySize());
    }

    @Test
    public void testDefragment() {
        LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        int max = LinkedVirtualMemoryTable.DEFRAGMENT_AFTER_FREES;
        int avgBlockSize = 200 / max;
        Block[] blocks = new Block[max];
        for (int i = 0; i < max; i++) {
            blocks[i] = table.allocate(avgBlockSize - i);
            if (i != 0) {
                table.free(blocks[i - 1]);
            }
        }


        Assert.assertEquals(1, table.getUsed().size());
        Assert.assertEquals(2, table.getFree().size());

        table.free(blocks[max - 1]);

        Assert.assertEquals(0, table.getUsed().size());
        Assert.assertEquals(1, table.getFree().size());

        table.defragment();

        Assert.assertEquals(0, table.getUsed().size());
        Assert.assertEquals(1, table.getFree().size());

        Assert.assertEquals(200, table.getFreeMemorySize());
        Assert.assertEquals(0, table.getUsedMemorySize());
    }

}
