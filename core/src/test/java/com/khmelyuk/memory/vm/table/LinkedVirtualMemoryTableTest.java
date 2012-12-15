package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.metrics.MetricsSnapshot;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;

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

        MetricsSnapshot metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalAllocations").get(), is(0L));

        table.allocate(100);
        metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalAllocations").get(), is(1L));

        table.allocate(50);
        metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalAllocations").get(), is(2L));
    }

    @Test
    public void failedAllocationsCounter() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        MetricsSnapshot metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalAllocations").get(), is(0L));

        table.allocate(300);
        metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalAllocations").get(), is(1L));
        assertThat(metrics.getValueMetric("failedAllocations").get(), is(1L));
    }

    @Test
    public void freesCounter() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        MetricsSnapshot metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalAllocations").get(), is(0L));

        table.free(table.allocate(100));
        metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalFrees").get(), is(1L));

        table.free(table.allocate(100));
        metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalFrees").get(), is(2L));
    }

    @Test
    public void failedFreesCounter() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        MetricsSnapshot metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalAllocations").get(), is(0L));

        Block block = table.allocate(100);
        assertTrue(table.free(block));
        assertFalse(table.free(block));

        metrics = table.getMetrics();
        assertThat(metrics.getValueMetric("totalFrees").get(), is(2L));
        assertThat(metrics.getValueMetric("failedFrees").get(), is(1L));
    }

    @Test
    public void resetTableResetsStatistic() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        table.allocate(20);
        MetricsSnapshot metrics = table.getMetrics();

        assertThat(metrics.getValueMetric("totalAllocations").get(), is(1L));
        assertThat(metrics.getValueMetric("failedAllocations").get(), is(0L));
        assertThat(metrics.getValueMetric("totalFrees").get(), is(0L));
        assertThat(metrics.getValueMetric("failedFrees").get(), is(0L));
        assertThat(metrics.getValueMetric("freeBlocksCount").get(), is(1L));
        assertThat(metrics.getValueMetric("usedBlocksCount").get(), is(1L));
        assertThat(metrics.getValueMetric("freeSize").get(), is(180L));
        assertThat(metrics.getValueMetric("usedSize").get(), is(20L));
        assertThat(metrics.getTimerMetric("vmtable.freeTime").getCount(), is(0L));
        assertThat(metrics.getTimerMetric("vmtable.allocationTime").getCount(), not(0L));

        table.reset(0);
        metrics = table.getMetrics();

        assertThat(metrics.getValueMetric("totalAllocations").get(), is(0L));
        assertThat(metrics.getValueMetric("failedAllocations").get(), is(0L));
        assertThat(metrics.getValueMetric("totalFrees").get(), is(0L));
        assertThat(metrics.getValueMetric("failedFrees").get(), is(0L));
        assertThat(metrics.getValueMetric("freeBlocksCount").get(), is(1L));
        assertThat(metrics.getValueMetric("usedBlocksCount").get(), is(0L));
        assertThat(metrics.getValueMetric("freeSize").get(), is(0L));
        assertThat(metrics.getValueMetric("usedSize").get(), is(0L));
        assertThat(metrics.getTimerMetric("vmtable.freeTime").getCount(), is(0L));
        assertThat(metrics.getTimerMetric("vmtable.allocationTime").getCount(), is(0L));
    }

    @Test
    public void fragmentationOnAllocate() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        table.allocate(100);
        assertThat(table.getMetrics().getValueMetric("fragmentation").get(), is(1L));

        table.allocate(100);
        assertThat(table.getMetrics().getValueMetric("fragmentation").get(), is(0L));
    }

    @Test
    public void fragmentationOnFree() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);

        Block block = table.allocate(100);
        assertThat(table.getMetrics().getValueMetric("fragmentation").get(), is(1L));

        table.free(block);
        assertThat(table.getMetrics().getValueMetric("fragmentation").get(), is(0L));

        block = table.allocate(100);
        Block block1 = table.allocate(50);
        assertThat(table.getMetrics().getValueMetric("fragmentation").get(), is(2L));

        table.free(block1);
        assertThat(table.getMetrics().getValueMetric("fragmentation").get(), is(1L));

        table.free(block);
        assertThat(table.getMetrics().getValueMetric("fragmentation").get(), is(0L));
    }

    @Test
    public void hasMetrics() {
        final LinkedVirtualMemoryTable table = new LinkedVirtualMemoryTable(200);
        MetricsSnapshot snapshot = table.getMetrics();

        assertThat(snapshot.getMetrics(), hasItem("totalAllocations"));
        assertThat(snapshot.getMetrics(), hasItem("failedAllocations"));
        assertThat(snapshot.getMetrics(), hasItem("totalFrees"));
        assertThat(snapshot.getMetrics(), hasItem("failedFrees"));
        assertThat(snapshot.getMetrics(), hasItem("increases"));
        assertThat(snapshot.getMetrics(), hasItem("loopsToFindFitBlock"));
        assertThat(snapshot.getMetrics(), hasItem("fragmentation"));
        assertThat(snapshot.getMetrics(), hasItem("vmtable.freeTime"));
        assertThat(snapshot.getMetrics(), hasItem("vmtable.allocationTime"));
    }

}
