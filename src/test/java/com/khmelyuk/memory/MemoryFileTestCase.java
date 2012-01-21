package com.khmelyuk.memory;

import com.khmelyuk.memory.space.Space;
import com.khmelyuk.memory.vm.FixedVirtualMemory;
import com.khmelyuk.memory.vm.storage.ByteBufferStorage;
import com.khmelyuk.memory.vm.storage.Storage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Ruslan Khmelyuk
 */
public class MemoryFileTestCase {

    @Test
    public void testBackedByFile() throws Exception {
        int size = 10000000;
        File file = new File("test.txt");
        FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
        Storage storage = new ByteBufferStorage(buffer);

        Memory memory = new Memory(
                new FixedVirtualMemory(storage,
                        new LinkedVirtualMemoryTable(size)));

        Space space = memory.allocate(200);
        space.write("Hello world of goo");

        space.free();

        channel.close();

        channel = new RandomAccessFile(file, "rw").getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
        storage = new ByteBufferStorage(buffer);

        memory = new Memory(
                new FixedVirtualMemory(storage,
                        new LinkedVirtualMemoryTable(size)));

        space = memory.allocate(200);
        Assert.assertEquals("Hello world of goo", space.readString());

        space.free();

        channel.close();
    }
}
