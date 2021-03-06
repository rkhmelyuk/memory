package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Ruslan Khmelyuk
 */
public class VMOutputStreamTest {

    @Test
    public void testWrite() throws Exception {
        VirtualMemory memory = createVirtualMemory(100);

        VMOutputStream s = new VMOutputStream(memory);
        s.write(new byte[]{10, 20, 30});

        Assert.assertEquals(10, memory.read(0));
        Assert.assertEquals(20, memory.read(1));
        Assert.assertEquals(30, memory.read(2));
    }

    @Test
    public void testWrite_Window() throws Exception {
        VirtualMemory memory = createVirtualMemory(100);

        VMOutputStream s = new VMOutputStream(memory, 10, 6);
        s.write(new byte[]{10, 20, 30, 40, 50});

        Assert.assertEquals(10, memory.read(10));
        Assert.assertEquals(20, memory.read(11));
        Assert.assertEquals(30, memory.read(12));
        Assert.assertEquals(40, memory.read(13));
        Assert.assertEquals(50, memory.read(14));
        Assert.assertEquals(0, memory.read(15));
    }

    @Test(expected = IOException.class)
    public void testWrite_Limited() throws Exception {
        VirtualMemory memory = createVirtualMemory(2);

        VMOutputStream s = new VMOutputStream(memory);
        s.write(new byte[]{10, 20, 30});

        Assert.fail("Must be failed, as IOException should be thrown!");
    }


    @Test(expected = IOException.class)
    public void testWrite_StreamLimited() throws Exception {
        VirtualMemory memory = createVirtualMemory(10);

        VMOutputStream s = new VMOutputStream(memory, 2, 2);
        s.write(new byte[]{10, 20, 30});

        Assert.fail("Must be failed, as IOException should be thrown!");
    }

    private VirtualMemory createVirtualMemory(int size) {
        return new FixedVirtualMemory(
                new ByteArrayStorage(size),
                new LinkedVirtualMemoryTable(size));
    }
}
