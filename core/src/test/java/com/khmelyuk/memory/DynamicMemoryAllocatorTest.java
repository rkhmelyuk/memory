package com.khmelyuk.memory;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class DynamicMemoryAllocatorTest {

    @Test
    public void testAllocateMemory() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20), MemorySize.kilobytes(40), MemorySize.kilobytes(5));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());
    }

    @Test
    public void allocateMemoryUsingMemorySize() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20), MemorySize.kilobytes(40), MemorySize.kilobytes(5));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());
    }

    @Test
    public void testAllocateFixedSize() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20), MemorySize.kilobytes(20));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());
    }

    @Test
    public void allocatedFixedSizeMemoryUsingMemorySize() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20), MemorySize.kilobytes(20));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());
    }

    @Test
    public void testAllocateMemory_Grow() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20), MemorySize.kilobytes(40), MemorySize.kilobytes(5));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());

        memory.allocate(MemorySize.kilobytes(40));
        Assert.assertEquals(MemorySize.kilobytes(40).getBytes(), memory.size());
    }

    @Test(expected = OutOfMemoryException.class)
    public void testAllocateMemory_OutOfMemory() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(MemorySize.kilobytes(20), MemorySize.kilobytes(40), MemorySize.kilobytes(5));

        Assert.assertNotNull(memory);
        Assert.assertEquals(MemorySize.kilobytes(20).getBytes(), memory.size());

        memory.allocate(MemorySize.kilobytes(20));
        memory.allocate(MemorySize.kilobytes(30));
    }
}
