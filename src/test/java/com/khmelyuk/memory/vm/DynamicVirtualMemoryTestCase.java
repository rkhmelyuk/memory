package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.storage.DynamicStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class DynamicVirtualMemoryTestCase extends VirtualMemoryTestCase {

    @Test
    public void testAllocate() {
        VirtualMemory memory = createVirtualMemory(100);

        memory.allocate(20);

        Assert.assertEquals(20, memory.getUsedSize());
        Assert.assertEquals(80, memory.getFreeSize());

        memory.allocate(200);

        Assert.assertEquals(220, memory.getUsedSize());
        Assert.assertEquals(80, memory.getFreeSize());

        memory.allocate(50);

        Assert.assertEquals(270, memory.getUsedSize());
        Assert.assertEquals(30, memory.getFreeSize());
    }

    @Test(expected = OutOfMemoryException.class)
    public void testAllocate_ToMuch() {
        VirtualMemory memory = createVirtualMemory(100);

        memory.allocate(5000);

        Assert.fail("Wow, it couldn't allocate that much");
    }

    @Test
    public void testLargeSize() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.allocate(20);
        VirtualMemoryBlock block = memory.allocate(200);

        byte[] data = generateData(200);
        block.write(data);

        byte[] read = new byte[200];
        block.read(read);

        Assert.assertArrayEquals(data, read);
    }

    @Test
    public void testLargeSize_Position0() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.allocate(20);
        memory.allocate(200);

        byte[] data = generateData(200);
        memory.write(data);

        byte[] read = new byte[200];
        memory.read(read);

        Assert.assertArrayEquals(data, read);
    }

    @Test
    public void testLargeSize_PositionN() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.allocate(20);
        memory.allocate(200);

        byte[] data = generateData(200);
        memory.write(generateData(200), 40);

        byte[] read = new byte[200];
        memory.read(read, 40, read.length);

        Assert.assertArrayEquals(data, read);
    }

    @Test
    public void testAllocateManyElements() {
        VirtualMemory memory = new DynamicVirtualMemory(
                new DynamicStorage(100, 10 * Memory.MB, 100),
                100, 10 * Memory.MB, 100,
                new LinkedVirtualMemoryTable(100));

        for (int i = 0; i < 1000; i++) {
            Assert.assertNotNull(memory.allocate(60));
        }
    }

    @Test
    public void testLargeSize_Single() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.allocate(20);
        memory.allocate(200);

        memory.write((byte) 112, 164);

        Assert.assertEquals((byte) 112, memory.read(164));
    }

    @Test
    public void testPlayWithDVM() {
        VirtualMemory memory = createVirtualMemory(140);
        VirtualMemoryBlock block0 = memory.allocate(20);
        VirtualMemoryBlock block1 = memory.allocate(200);
        VirtualMemoryBlock block2 = memory.allocate(20);
        VirtualMemoryBlock block3 = memory.allocate(120);
        VirtualMemoryBlock block4 = memory.allocate(150);

        byte[] data0 = generateData(20);
        byte[] data1 = generateData(200);
        byte[] data2 = generateData(20);
        byte[] data3 = generateData(120);
        byte[] data4 = generateData(150);

        byte[] read0 = new byte[20];
        byte[] read1 = new byte[200];
        byte[] read2 = new byte[20];
        byte[] read3 = new byte[120];
        byte[] read4 = new byte[150];

        block0.write(data0);
        block1.write(data1);
        block2.write(data2);
        block3.write(data3);
        block4.write(data4);

        block0.read(read0);
        block1.read(read1);
        block2.read(read2);
        block3.read(read3);
        block4.read(read4);

        Assert.assertArrayEquals(data0, read0);
        Assert.assertArrayEquals(data1, read1);
        Assert.assertArrayEquals(data2, read2);
        Assert.assertArrayEquals(data3, read3);
        Assert.assertArrayEquals(data4, read4);
    }

    private byte[] generateData(int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = (byte) i;
        }
        return array;
    }

    protected VirtualMemory createVirtualMemory(int size) {
        return new DynamicVirtualMemory(
                new DynamicStorage(size, size * 5, size),
                size, size * 5, size,
                new LinkedVirtualMemoryTable(size));
    }
}
