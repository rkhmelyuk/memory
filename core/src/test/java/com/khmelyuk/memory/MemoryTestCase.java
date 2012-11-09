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
public class MemoryTestCase {

    @Test
    public void testAlloc() {

        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        Assert.assertNotNull(memory);
        Assert.assertEquals(20 * Memory.KB, memory.size());
    }

    @Test
    public void testFree() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        Assert.assertNotNull(memory);
        Assert.assertEquals(20 * Memory.KB, memory.size());

        memory.free();

        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void testAllocSpace() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        MemorySpace space = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space);
        Assert.assertEquals(5 * Memory.KB, space.size());
        Assert.assertEquals(15 * Memory.KB, memory.getFreeMemorySize());
    }

    @Test
    public void testAllocSpaces() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        MemorySpace space = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(5 * Memory.KB, space.size());
        Assert.assertEquals(15 * Memory.KB, memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space2);
        Assert.assertEquals(5 * Memory.KB, space2.size());
        Assert.assertEquals(5 * Memory.KB, space2.getAddress());
        Assert.assertEquals(10 * Memory.KB, memory.getFreeMemorySize());
    }

    @Test(expected = OutOfMemoryException.class)
    public void testAllocSpace_NoSize() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        memory.allocate(25 * Memory.KB);
    }

    @Test(expected = OutOfMemoryException.class)
    public void testAllocSpace_OutOfMemory() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        MemorySpace s = memory.allocate(10 * Memory.KB);
        Assert.assertNotNull(s);
        s = memory.allocate(10 * Memory.KB);
        Assert.assertNotNull(s);
        memory.allocate(10 * Memory.KB);
    }

    @Test
    public void testFreeSpaces() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        MemorySpace space = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(5 * Memory.KB, space.size());
        Assert.assertEquals(15 * Memory.KB, memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space2);
        Assert.assertEquals(5 * Memory.KB, space2.size());
        Assert.assertEquals(5 * Memory.KB, space2.getAddress());
        Assert.assertEquals(10 * Memory.KB, memory.getFreeMemorySize());

        space2.free();

        Assert.assertEquals(0, space2.size());
        Assert.assertEquals(0, space2.getAddress());
        Assert.assertEquals(15 * Memory.KB, memory.getFreeMemorySize());

        MemorySpace space3 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space3);
        Assert.assertEquals(5 * Memory.KB, space3.size());
        Assert.assertEquals(5 * Memory.KB, space3.getAddress());
        Assert.assertEquals(10 * Memory.KB, memory.getFreeMemorySize());
    }

    @Test
    public void testAllocateInside() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        MemorySpace space = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(5 * Memory.KB, space.size());
        Assert.assertEquals(15 * Memory.KB, memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space2);
        Assert.assertEquals(5 * Memory.KB, space2.size());
        Assert.assertEquals(5 * Memory.KB, space2.getAddress());
        Assert.assertEquals(10 * Memory.KB, memory.getFreeMemorySize());

        MemorySpace space3 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space3);
        Assert.assertEquals(5 * Memory.KB, space3.size());
        Assert.assertEquals(10 * Memory.KB, space3.getAddress());
        Assert.assertEquals(5 * Memory.KB, memory.getFreeMemorySize());

        space2.free();

        Assert.assertEquals(0, space2.size());
        Assert.assertEquals(0, space2.getAddress());
        Assert.assertEquals(10 * Memory.KB, memory.getFreeMemorySize());

        MemorySpace space4 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space4);
        Assert.assertEquals(5 * Memory.KB, space4.size());
        Assert.assertEquals(5 * Memory.KB, space4.getAddress());
        Assert.assertEquals(5 * Memory.KB, memory.getFreeMemorySize());
    }

    @Test
    public void testAllocateBegin() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB);

        MemorySpace space = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space);
        Assert.assertEquals(0, space.getAddress());
        Assert.assertEquals(5 * Memory.KB, space.size());
        Assert.assertEquals(15 * Memory.KB, memory.getFreeMemorySize());

        MemorySpace space2 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space2);
        Assert.assertEquals(5 * Memory.KB, space2.size());
        Assert.assertEquals(5 * Memory.KB, space2.getAddress());
        Assert.assertEquals(10 * Memory.KB, memory.getFreeMemorySize());

        space.free();

        MemorySpace space3 = memory.allocate(5 * Memory.KB);

        Assert.assertNotNull(space3);
        Assert.assertEquals(5 * Memory.KB, space3.size());
        Assert.assertEquals(0, space3.getAddress());
        Assert.assertEquals(10 * Memory.KB, memory.getFreeMemorySize());
        Assert.assertEquals(10 * Memory.KB, memory.getUsedMemorySize());
    }

    @Test
    public void testGetStatistic() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();

        Memory memory = allocator.allocate(20 * Memory.KB);
        Assert.assertNotNull(memory);

        Space space = memory.allocate(20 * Memory.KB);
        Assert.assertNotNull(space);

        MemoryStatistic stats = memory.getStatistic();

        Assert.assertEquals(0, stats.getFreeSize());
        Assert.assertEquals(20 * Memory.KB, stats.getUsedSize());
        Assert.assertEquals(0, stats.getFreeBlocksCount());
        Assert.assertEquals(1, stats.getUsedBlocksCount());
        Assert.assertEquals(1, stats.getSuccessAllocations());

        stats.print();

        space.free();

        stats = memory.getStatistic();

        Assert.assertEquals(20 * Memory.KB, stats.getFreeSize());
        Assert.assertEquals(0, stats.getUsedSize());
        Assert.assertEquals(1, stats.getFreeBlocksCount());
        Assert.assertEquals(0, stats.getUsedBlocksCount());
        Assert.assertEquals(1, stats.getSuccessAllocations());
    }
}
