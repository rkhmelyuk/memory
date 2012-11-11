package com.khmelyuk.memory;

import com.khmelyuk.memory.vm.FixedVirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;
import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of tests to work how it works in concurrent environment.
 *
 * @author Ruslan Khmelyuk
 */
public class ConcurrencyTestCase {

    private static final int SIZE = MemorySize.kilobytes(2).getBytes();

    private Memory memory;

    @Before
    public void setUp() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        memory = allocator.allocate(MemorySize.kilobytes(20));
    }

    @After
    public void tearDown() {
        memory.free();
    }

    @Test(timeout = 3000)
    public void testVMBlockReadWrite() throws Exception {
        final VirtualMemory vm = createVirtualMemory();
        final VirtualMemoryBlock block = vm.allocate(100);

        int count = 20;
        List<Thread> threads = new ArrayList<Thread>(count);
        for (int i = 0; i < count; i++) {
            Thread thread = new TestBlockReadWrite(block, i);
            threads.add(thread);
            thread.start();
        }

        Thread.sleep(2000);

        for (Thread each : threads) {
            each.interrupt();
        }
    }

    @Test(timeout = 3000)
    public void testVMTable() throws Exception {
        VirtualMemoryTable table = new LinkedVirtualMemoryTable(1000);

        int count = 20;
        List<Thread> threads = new ArrayList<Thread>(count);
        for (int i = 0; i < count; i++) {
            Thread thread = new TestTableAllocationFree(table, i);
            threads.add(thread);
            thread.start();
        }

        Thread.sleep(2000);

        for (Thread each : threads) {
            each.interrupt();
        }
    }

    private static VirtualMemory createVirtualMemory() {
        return new FixedVirtualMemory(
                new ByteArrayStorage(SIZE),
                new LinkedVirtualMemoryTable(SIZE));
    }

    private static class TestBlockReadWrite extends Thread {

        private VirtualMemoryBlock block;
        private int value;

        public TestBlockReadWrite(VirtualMemoryBlock block, int value) {
            this.block = block;
            this.value = value;
        }

        @Override
        public void run() {
            final int size = block.size();

            byte[] data = new byte[size];
            for (int i = 0; i < size; i++) {
                data[i] = (byte) value;
            }
            byte[] buff = new byte[size];
            while (!isInterrupted()) {
                block.write(data);

                // test read lock works correctly - we read exactly as it's when start reading.
                block.read(buff);
                for (int i = 1; i < size; i++) {
                    Assert.assertEquals(buff[0], buff[i]);
                }

                // test dump - we dump memory exactly as it was when we started.
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream(size);
                    block.dump(os);
                    byte[] osData = os.toByteArray();
                    for (int i = 1; i < osData.length; i++) {
                        Assert.assertEquals(osData[0], osData[i]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class TestTableAllocationFree extends Thread {

        private VirtualMemoryTable table;
        private int value;

        public TestTableAllocationFree(VirtualMemoryTable table, int value) {
            this.table = table;
            this.value = value;
        }

        @Override
        public void run() {
            Block freeBlock = null;
            while (!isInterrupted()) {
                Block block = table.allocate((int) (Math.random() * 30) + 10);
                if (freeBlock != null) {
                    table.free(freeBlock);
                    freeBlock = null;
                }
                if (block != null) {
                    // lets test it is not used yet
                    freeBlock = block;
                }
            }
        }
    }
}
