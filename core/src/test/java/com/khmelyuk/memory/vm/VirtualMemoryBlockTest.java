package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Ruslan Khmelyuk
 */
public class VirtualMemoryBlockTest {

    @Test
    public void testGetInputStream() throws Exception {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block = memory.allocate(5);

        block.write(new byte[]{10, 20, 30, 40});

        InputStream stream = block.getInputStream();
        Assert.assertEquals(10, stream.read());
        Assert.assertEquals(20, stream.read());
        Assert.assertEquals(30, stream.read());
        Assert.assertEquals(40, stream.read());
    }

    @Test
    public void testGetOutputStream() throws Exception {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block = memory.allocate(5);

        block.write(new byte[]{10, 20, 30, 40});

        OutputStream stream = block.getOutputStream();
        stream.write(new byte[]{10, 20, 30, 40});

        byte[] read = new byte[4];
        block.read(read);

        Assert.assertEquals(10, read[0]);
        Assert.assertEquals(20, read[1]);
        Assert.assertEquals(30, read[2]);
        Assert.assertEquals(40, read[3]);
    }

    @Test(expected = OutOfBoundException.class)
    public void testWriteTooMuch() {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block = memory.allocate(5);

        block.write(new byte[]{10, 20, 30, 40, 50, 60});
    }

    @Test
    public void testWriteWithOffsetAndLength() {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block = memory.allocate(5);

        block.write(new byte[]{10, 20, 30}, 2, 2);

        byte[] read = new byte[2];
        block.read(read, 2, 2);

        Assert.assertEquals(10, read[0]);
        Assert.assertEquals(20, read[1]);
    }

    @Test
    public void testWriteWithWrongLength() {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block = memory.allocate(5);

        block.write(new byte[]{10, 20, 30}, 2, 10);

        byte[] read = new byte[3];
        block.read(read, 2, 3);

        Assert.assertEquals(10, read[0]);
        Assert.assertEquals(20, read[1]);
        Assert.assertEquals(30, read[2]);
    }

    @Test(expected = OutOfBoundException.class)
    public void testWriteOutOfBound() {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block = memory.allocate(5);

        block.write(new byte[]{10, 20, 30}, 3, 3);
    }

    @Test
    public void testReadWithOffsetAndLengthOutOfBound() {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block1 = memory.allocate(5);
        VirtualMemoryBlock block2 = memory.allocate(5);

        block1.write(new byte[]{10, 20, 30}, 2, 3);
        block2.write(new byte[]{10, 20, 30});

        block1.read(new byte[5], 2, 5);
    }

    @Test
    public void testReadOutOfBound() {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block1 = memory.allocate(5);
        VirtualMemoryBlock block2 = memory.allocate(5);

        block1.write(new byte[]{10, 20, 30}, 2, 3);
        block2.write(new byte[]{10, 20, 30});

        block1.read(new byte[10]);
    }

    @Test
    public void testDump() throws Exception {
        VirtualMemory memory = createFixedVirtualMemory(10);
        VirtualMemoryBlock block = memory.allocate(5);
        byte[] data = {10, 20, 30, 40, 50};

        block.write(data);

        ByteArrayOutputStream out = new ByteArrayOutputStream(5);
        block.dump(out);

        byte[] read = out.toByteArray();
        Assert.assertArrayEquals(data, read);
    }

    private VirtualMemory createFixedVirtualMemory(int size) {
        return new FixedVirtualMemory(
                new ByteArrayStorage(size),
                new LinkedVirtualMemoryTable(size));
    }
}
