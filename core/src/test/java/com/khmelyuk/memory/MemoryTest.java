package com.khmelyuk.memory;

import com.khmelyuk.memory.space.MemorySpace;
import com.khmelyuk.memory.space.Space;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for Memory class.
 *
 * @author Ruslan Khmelyuk
 */
public class MemoryTest {

    @Test
    public void testAlloc() {

        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());
    }

    @Test
    public void testFree() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());

        memory.free();

        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void allocate() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        MemorySpace space = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space.size());
        Assert.assertEquals(MemorySize.kilobytes(15).getBytes(), memory.getFreeMemorySize());
    }

    @Test
    public void allocateUsingMemorySize() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        MemorySpace space = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space.size());
        Assert.assertEquals(MemorySize.kilobytes(15).getBytes(), memory.getFreeMemorySize());
    }

    @Test
    public void testAllocSpaces() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        MemorySpace space = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space.size());
        Assert.assertEquals(MemorySize.kilobytes(15).getBytes(), memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space2);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.size());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getFreeMemorySize());
    }

    @Test(expected = OutOfMemoryException.class)
    public void testAllocSpace_NoSize() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        memory.allocate(MemorySize.kilobytes(25));
    }

    @Test(expected = OutOfMemoryException.class)
    public void testAllocSpace_OutOfMemory() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        MemorySpace s = memory.allocate(MemorySize.kilobytes(10));
        Assert.assertNotNull(s);
        s = memory.allocate(MemorySize.kilobytes(10));
        Assert.assertNotNull(s);
        memory.allocate(MemorySize.kilobytes(10));
    }

    @Test
    public void testFreeSpaces() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        MemorySpace space = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space.size());
        Assert.assertEquals(MemorySize.kilobytes(15).getBytes(), memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space2);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.size());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getFreeMemorySize());

        space2.free();

        Assert.assertEquals(0, space2.size());
        Assert.assertEquals(0, space2.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(15).getBytes(), memory.getFreeMemorySize());

        MemorySpace space3 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space3);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space3.size());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space3.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getFreeMemorySize());
    }

    @Test
    public void testAllocateInside() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        MemorySpace space = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space.size());
        Assert.assertEquals(MemorySize.kilobytes(15).getBytes(), memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space2);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.size());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getFreeMemorySize());

        MemorySpace space3 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space3);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space3.size());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), space3.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), memory.getFreeMemorySize());

        space2.free();

        Assert.assertEquals(0, space2.size());
        Assert.assertEquals(0, space2.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getFreeMemorySize());

        MemorySpace space4 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space4);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space4.size());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space4.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), memory.getFreeMemorySize());
    }

    @Test
    public void testAllocateBegin() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20));

        MemorySpace space = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space.size());
        Assert.assertEquals(MemorySize.kilobytes(15).getBytes(), memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space2);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.size());
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space2.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getFreeMemorySize());

        space.free();

        MemorySpace space3 = memory.allocate(MemorySize.kilobytes(5));

        Assert.assertNotNull(space3);
        Assert.assertEquals(MemorySize.kilobytes(5).getBytes(), space3.size());
        Assert.assertEquals(0, space3.getAddress());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getFreeMemorySize());
        Assert.assertEquals(MemorySize.kilobytes(10).getBytes(), memory.getUsedMemorySize());
    }

    @Test
    public void testGetStatistic() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();

        Memory memory = allocator.allocate(MemorySize.kilobytes(20));
        Assert.assertNotNull(memory);

        Space space = memory.allocate(MemorySize.kilobytes(20));
        Assert.assertNotNull(space);

        MemoryStatistic stats = memory.getStatistic();

        Assert.assertEquals(MemorySize.ZERO, stats.getFreeSize());
        Assert.assertEquals(MemorySize.kilobytes(20), stats.getUsedSize());
        Assert.assertEquals(0, stats.getFreeBlocksCount());
        Assert.assertEquals(1, stats.getUsedBlocksCount());
        Assert.assertEquals(1, stats.getSuccessAllocations());

        stats.print();

        space.free();

        stats = memory.getStatistic();

        Assert.assertEquals(MemorySize.kilobytes(20), stats.getFreeSize());
        Assert.assertEquals(MemorySize.ZERO, stats.getUsedSize());
        Assert.assertEquals(1, stats.getFreeBlocksCount());
        Assert.assertEquals(0, stats.getUsedBlocksCount());
        Assert.assertEquals(1, stats.getSuccessAllocations());
    }
}
