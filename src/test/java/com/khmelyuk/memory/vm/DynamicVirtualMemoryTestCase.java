package com.khmelyuk.memory.vm;

import org.junit.Assert;
import org.junit.Test;

import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

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
        block.write(generateData(200));

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
        memory.write(generateData(200));

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
    public void testLargeSize_Single() {
        VirtualMemory memory = createVirtualMemory(100);
        memory.allocate(20);
        memory.allocate(200);

        memory.write((byte) 112, 164);

        Assert.assertEquals((byte) 112, memory.read(164));
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
                size, size * 5, size,
                new LinkedVirtualMemoryTable(size));
    }
}
