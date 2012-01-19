package com.khmelyuk.memory;

import com.khmelyuk.memory.vm.FixedVirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
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

    private static final int SIZE = 2 * Memory.KB;

    private Memory memory;

    @Before
    public void setUp() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        memory = allocator.allocate(20 * Memory.KB);
    }

    @After
    public void tearDown() {
        memory.free();
    }

    @Test(timeout = 6000)
    public void testVMBlockReadWrite() throws Exception {
        final VirtualMemory vm = createVirtualMemory();
        final VirtualMemoryBlock block = vm.allocate(100);

        int count = 20;
        List<Thread> threads = new ArrayList<Thread>(count);
        for (int i = 0; i < count; i++) {
            Thread thread = new FillMemory(block, i);
            threads.add(thread);
            thread.start();
        }

        Thread.sleep(5000);

        for (Thread each : threads) {
            each.interrupt();
        }
    }

    private static VirtualMemory createVirtualMemory() {
        return new FixedVirtualMemory(SIZE,
                new LinkedVirtualMemoryTable(SIZE));
    }

    private static class FillMemory extends Thread {

        private VirtualMemoryBlock block;
        private int value;

        public FillMemory(VirtualMemoryBlock block, int value) {
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
            while(!isInterrupted()) {
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
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
