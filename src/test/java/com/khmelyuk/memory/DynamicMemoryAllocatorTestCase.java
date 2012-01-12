package com.khmelyuk.memory;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class DynamicMemoryAllocatorTestCase {

    @Test
    public void testAllocateMemory() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB, 40 * Memory.KB, 5 * Memory.KB);

        Assert.assertNotNull(memory);
        Assert.assertEquals(20 * Memory.KB, memory.size());
    }

    @Test
    public void testAllocateMemory_Grow() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB, 40 * Memory.KB, 5 * Memory.KB);

        Assert.assertNotNull(memory);
        Assert.assertEquals(20 * Memory.KB, memory.size());

        memory.allocate(40 * Memory.KB);
        Assert.assertEquals(40 * Memory.KB, memory.size());
    }

    @Test(expected = OutOfMemoryException.class)
    public void testAllocateMemory_OutOfMemory() {
        DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
        Memory memory = allocator.allocate(20 * Memory.KB, 40 * Memory.KB, 5 * Memory.KB);

        Assert.assertNotNull(memory);
        Assert.assertEquals(20 * Memory.KB, memory.size());

        memory.allocate(20 * Memory.KB);
        memory.allocate(30 * Memory.KB);
    }
}
