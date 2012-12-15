package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.metrics.MetricsSnapshot;
import org.junit.Assert;
import org.junit.Test;

/**
 * Base test case for virtual memory implementations.
 *
 * @author Ruslan Khmelyuk
 */
public abstract class VirtualMemoryTest {

    protected abstract VirtualMemory createVirtualMemory(int size);

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

    @Test
    public void testReadWriteBytes() {
        VirtualMemory memory = createVirtualMemory(100);

        memory.write(new byte[]{10, 20, 30});

        byte[] data = new byte[3];
        Assert.assertEquals(3, memory.read(data));
        Assert.assertEquals(10, data[0]);
        Assert.assertEquals(20, data[1]);
        Assert.assertEquals(30, data[2]);
    }

    @Test
    public void testReadWriteByte() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.write((byte) 10, 4);
        Assert.assertEquals((byte) 10, memory.read(4));
    }

    @Test
    public void testReadWriteBytesWithOffset() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.write(new byte[]{10, 20, 30, 40}, 4);

        byte[] data = new byte[4];
        Assert.assertEquals(4, memory.read(data, 4, 4));
        Assert.assertEquals(10, data[0]);
        Assert.assertEquals(20, data[1]);
        Assert.assertEquals(30, data[2]);
        Assert.assertEquals(40, data[3]);
    }

    @Test
    public void testGetStatistic() {
        VirtualMemory memory = createVirtualMemory(100);
        VirtualMemoryBlock block1 = memory.allocate(30);
        VirtualMemoryBlock block2 = memory.allocate(50);

        Assert.assertNotNull(block1);
        Assert.assertNotNull(block2);

        MetricsSnapshot metrics = memory.getMetrics();

        Assert.assertEquals(20L, metrics.getValueMetric("freeSize").get());
        Assert.assertEquals(80L, metrics.getValueMetric("usedSize").get());
        Assert.assertEquals(1L, metrics.getValueMetric("freeBlocksCount").get());
        Assert.assertEquals(2L, metrics.getValueMetric("usedBlocksCount").get());
    }

}
