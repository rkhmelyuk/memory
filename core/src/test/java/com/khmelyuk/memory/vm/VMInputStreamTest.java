package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class VMInputStreamTest {

    @Test
    public void testRead() throws Exception {
        VirtualMemory memory = createVirtualMemory(100);
        memory.write(new byte[]{10, 20, 30});

        VMInputStream s = new VMInputStream(memory);

        int count = 0, c;
        while ((c = s.read()) != -1) {
            if (count == 0) {
                Assert.assertEquals(10, c);
            } else if (count == 1) {
                Assert.assertEquals(20, c);
            } else if (count == 2) {
                Assert.assertEquals(30, c);
            } else {
                Assert.assertEquals(0, c);
            }
            count++;
        }

        Assert.assertEquals(100, count);
    }

    @Test
    public void testRead_Limited() throws Exception {
        VirtualMemory memory = createVirtualMemory(100);
        memory.write(new byte[]{10, 20, 30});

        VMInputStream s = new VMInputStream(memory, 0, 3);

        int count = 0, c;
        while ((c = s.read()) != -1) {
            if (count == 0) {
                Assert.assertEquals(10, c);
            } else if (count == 1) {
                Assert.assertEquals(20, c);
            } else if (count == 2) {
                Assert.assertEquals(30, c);
            }
            count++;
        }

        Assert.assertEquals(3, count);
    }

    @Test
    public void testRead_Window() throws Exception {
        VirtualMemory memory = createVirtualMemory(100);
        memory.write(new byte[]{10, 20, 30});

        VMInputStream s = new VMInputStream(memory, 2, 3);

        int count = 0, c;
        while ((c = s.read()) != -1) {
            if (count == 0) {
                Assert.assertEquals(30, c);
            }
            count++;
        }

        Assert.assertEquals(3, count);
    }

    @Test
    public void testSkip() throws Exception {
        VirtualMemory memory = createVirtualMemory(100);
        VMInputStream s = new VMInputStream(memory);

        Assert.assertEquals(10, s.skip(10));
        Assert.assertEquals(90, s.skip(100));
    }

    @Test
    public void testReset() throws Exception {
        VirtualMemory memory = createVirtualMemory(100);
        VMInputStream s = new VMInputStream(memory);

        Assert.assertEquals(10, s.skip(10));
        Assert.assertEquals(90, s.available());

        s.reset();
        Assert.assertEquals(100, s.available());
    }

    private VirtualMemory createVirtualMemory(int size) {
        return new FixedVirtualMemory(
                new ByteArrayStorage(size),
                new LinkedVirtualMemoryTable(size));
    }
}
