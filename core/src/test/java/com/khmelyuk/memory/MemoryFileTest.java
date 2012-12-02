package com.khmelyuk.memory;

import com.khmelyuk.memory.space.Space;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * @author Ruslan Khmelyuk
 */
public class MemoryFileTest {

    private final FileMemoryAllocator allocator = new FileMemoryAllocator();

    @Test
    public void testFixedSizedFileMemory() throws Exception {
        int size = 1000;

        File file = new File("file-memory.test");
        Memory memory = allocator.allocate(file, size);

        Space space = memory.allocate(200);
        space.write("Hello world of goo");

        space.free();
        memory.free();

        memory = allocator.allocate(file, size);

        space = memory.allocate(200);
        Assert.assertEquals("Hello world of goo", space.readString());

        space.free();
        memory.free();

        file.deleteOnExit();
    }

    @Test
    public void testDynamicSizedFileMemory() throws Exception {
        int size = 1000;

        File file = new File("dynamic-file-memory.test");
        Memory memory = allocator.allocate(file, size, 10000);

        for (int i = 0; i < 20; i++) {
            Assert.assertNotNull(memory.allocate(200));
        }

        Assert.assertEquals(4000, memory.size());

        memory.free();

        Assert.assertEquals(4000, file.length());
        file.deleteOnExit();
    }

    @Test
    public void testDynamicSizedFileMemory_Write() throws Exception {
        int size = 1000;

        File file = new File("dynamic-file-memory-write.test");
        Memory memory = allocator.allocate(file, size, 10000);

        final byte[] data = new byte[175];
        for (int j = 0; j < 175; j++) {
            data[j] = (byte) j;
        }
        for (int i = 0; i < 20; i++) {
            Space space = memory.allocate(175);
            space.getBlock().write(data);
        }

        Assert.assertEquals(4000, memory.size());

        memory.free();

        Assert.assertEquals(4000, file.length());

        memory = allocator.allocate(file, size, 10000);
        for (int i = 0; i < 20; i++) {
            Space space = memory.allocate(175);
            byte[] read = new byte[175];
            space.getBlock().read(read);
            Assert.assertArrayEquals(data, read);
        }

        file.deleteOnExit();
    }
}
