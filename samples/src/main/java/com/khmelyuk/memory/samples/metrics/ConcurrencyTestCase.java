package com.khmelyuk.memory.samples.metrics;

import com.khmelyuk.memory.MemorySize;
import com.khmelyuk.memory.vm.FixedVirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;
import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A set of tests to work how it works in concurrent environment.
 *
 * @author Ruslan Khmelyuk
 */
public class ConcurrencyTestCase {

    private static final int SIZE = MemorySize.kilobytes(2).getBytes();

    @Test
    @Ignore
    public void testVMBlockReadWrite() throws Exception {
        final VirtualMemory vm = createVirtualMemory();
        final VirtualMemoryBlock block = vm.allocate(100);

        MemoryMetrics.init("concurrency.block_io", 500);
        MemoryMetrics.monitor(vm);
        MemoryMetrics.monitorSystemResources();

        int count = 20;
        List<Thread> threads = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Thread thread = new TestBlockReadWrite(block, i);
            threads.add(thread);
            thread.start();
        }

        Thread.sleep(10000);

        for (Thread each : threads) {
            each.interrupt();
        }
    }

    @Test
    public void testVMTable() throws Exception {
        VirtualMemoryTable table = new LinkedVirtualMemoryTable(1200);

        for (int i = 0; i < 10; i++) {
            new TestTableAllocationFree(table);
        }

        table = new LinkedVirtualMemoryTable(1200);

        MemoryMetrics.init("concurrency.vmtable", 200);
        MemoryMetrics.monitor(table);
        MemoryMetrics.monitorSystemResources();

        int count = 10;
        List<Thread> threads = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Thread thread = new TestTableAllocationFree(table);
            threads.add(thread);
            thread.start();
        }

        Thread.sleep(10000);

        for (Thread each : threads) {
            each.interrupt();
        }

        Collection<Block> used = new ArrayList<>(table.getUsed());
        for (Block each : used) {
            table.free(each);
        }
        Thread.sleep(100);

        used = new ArrayList<>(table.getUsed());
        for (Block each : used) {
            table.free(each);
        }
        Thread.sleep(200);
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

        public TestTableAllocationFree(VirtualMemoryTable table) {
            this.table = table;
        }

        @Override
        public void run() {
            Block prevBlock = null;
            int i = 0;
            while (!isInterrupted()) {
                //MemoryMetrics.startTimer("allocateBlock");
                Block block = table.allocate((int) (Math.random() * 30) + 50);
                //MemoryMetrics.stopTimer("allocateBlock");
                if (prevBlock != null) {
                    //MemoryMetrics.startTimer("freeBlock");
                    table.free(prevBlock);
                    //MemoryMetrics.stopTimer("freeBlock");
                    prevBlock = null;
                }
                if (block != null) {
                    // lets test it is not used yet
                    prevBlock = block;
                }
                i++;
            }
        }
    }
}
