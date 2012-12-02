package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.MemorySize;
import com.khmelyuk.memory.vm.VirtualMemoryStatistic;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Ruslan Khmelyuk
 */
public class LinkedVirtualMemoryTableTest {

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
    public void testFreeAvoidsFragmentation() {
        LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        int max = 20;
        int avgBlockSize = 200 / max;
        Block[] blocks = new Block[max];
        for (int i = 0; i < max; i++) {
            blocks[i] = table.allocate(avgBlockSize - i / 2);
            if (i != 0) {
                table.free(blocks[i - 1]);
            }
        }

        table.free(blocks[max - 1]);

        Assert.assertEquals(0, table.getUsed().size());
        Assert.assertEquals(1, table.getFree().size());

        Assert.assertEquals(200, table.getFreeMemorySize());
        Assert.assertEquals(0, table.getUsedMemorySize());
    }

    @Test
    public void testResetTable() {
        LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        Block block = table.allocate(20);

        assertNotNull(block);
        assertEquals(0, block.getAddress());
        assertEquals(20, block.getSize());
        assertEquals(180, table.getFreeMemorySize());
        assertEquals(20, table.getUsedMemorySize());

        table.reset(0);

        assertEquals(0, table.getFreeMemorySize());
        assertEquals(0, table.getUsed().size());
        assertEquals(1, table.getFree().size());
    }

    @Test
    public void testGrow() {
        LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        table.allocate(20);

        Assert.assertTrue(table.canIncreaseSize(220));
        table.increaseSize(220);
        Assert.assertEquals(200, table.getFreeMemorySize());

        Assert.assertFalse(table.canIncreaseSize(100));
    }

    @Test
    public void allocationsCounter() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        final VirtualMemoryStatistic stat = new VirtualMemoryStatistic();

        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalAllocations(), is(0L));

        table.allocate(100);
        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalAllocations(), is(1L));

        table.allocate(50);
        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalAllocations(), is(2L));
    }

    @Test
    public void failedAllocationsCounter() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        final VirtualMemoryStatistic stat = new VirtualMemoryStatistic();

        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalAllocations(), is(0L));

        table.allocate(300);
        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalAllocations(), is(1L));
        assertThat(stat.getFailedAllocations(), is(1L));
    }

    @Test
    public void freesCounter() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        final VirtualMemoryStatistic stat = new VirtualMemoryStatistic();

        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalAllocations(), is(0L));

        table.free(table.allocate(100));
        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalFrees(), is(1L));

        table.free(table.allocate(100));
        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalFrees(), is(2L));
    }

    @Test
    public void failedFreesCounter() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        final VirtualMemoryStatistic stat = new VirtualMemoryStatistic();

        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalAllocations(), is(0L));

        Block block = table.allocate(100);
        assertTrue(table.free(block));
        assertFalse(table.free(block));

        table.fillStatisticInformation(stat);
        assertThat(stat.getTotalFrees(), is(2L));
        assertThat(stat.getFailedFrees(), is(1L));
    }

    @Test
    public void resetTableResetsStatistic() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        final VirtualMemoryStatistic stat = new VirtualMemoryStatistic();

        table.allocate(20);
        table.fillStatisticInformation(stat);

        assertThat(stat.getTotalAllocations(), is(1L));
        assertThat(stat.getFailedAllocations(), is(0L));
        assertThat(stat.getTotalFrees(), is(0L));
        assertThat(stat.getFailedFrees(), is(0L));
        assertThat(stat.getFreeBlocksCount(), is(1));
        assertThat(stat.getUsedBlocksCount(), is(1));
        assertThat(stat.getFreeSize(), is(MemorySize.bytes(180)));
        assertThat(stat.getUsedSize(), is(MemorySize.bytes(20)));

        table.reset(0);
        table.fillStatisticInformation(stat);

        assertThat(stat.getTotalAllocations(), is(0L));
        assertThat(stat.getFailedAllocations(), is(0L));
        assertThat(stat.getTotalFrees(), is(0L));
        assertThat(stat.getFailedFrees(), is(0L));
        assertThat(stat.getFreeBlocksCount(), is(1));
        assertThat(stat.getUsedBlocksCount(), is(0));
        assertThat(stat.getFreeSize(), is(MemorySize.bytes(200)));
        assertThat(stat.getUsedSize(), is(MemorySize.bytes(0)));
    }

}
