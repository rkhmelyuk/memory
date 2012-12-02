package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.MemorySize;
import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.storage.ByteArrayStorageFactory;
import com.khmelyuk.memory.vm.storage.ByteBufferStorage;
import com.khmelyuk.memory.vm.storage.DynamicStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Test performance for different linked virtual memory tables
 *
 * @author Ruslan Khmelyuk
 */
public class VMPerformanceTest {

    static final int N = 5;
    static final int SIZE = MemorySize.megabytes(2).getBytes();
    static final int COUNT_COEFF = 2000;

    @Test
    public void testInitPerformance() {
        testPerformance(createFixedVirtualMemory(), 0);
        for (int i = 0; i < N; i++) {
            testPerformance(createFixedVirtualMemory(), i);
        }
    }

    @Test
    public void testFixedBufferVMPerformance() {
        testPerformance(createFixedBBVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createFixedBBVirtualMemory(), i);
        }

        System.out.println("Fixed byte buffer: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testFixedArrayVMPerformance() {
        testPerformance(createFixedVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createFixedVirtualMemory(), i);
        }

        System.out.println("Fixed byte array: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testFixedFileVMPerformance() {
        testPerformance(createFixedFileVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createFixedFileVirtualMemory(), i);
        }

        System.out.println("Fixed file: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testDynamicVMPerformance() {
        testPerformance(createDynamicVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createDynamicVirtualMemory(), i);
        }

        System.out.println("Dynamic: Avg. duration " + (total / N) + "ms");
    }

    private long testPerformance(VirtualMemory vm, int n) {
        int max = COUNT_COEFF * (n + 5);
        int avgBlockSize = SIZE / max;

        long begin = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            VirtualMemoryBlock block = vm.allocate(avgBlockSize);
            if (block != null) {
                write(block);
            }
            if (i % 10 == 0) {
                vm.free(block);
            }
        }
        try {
            return (System.currentTimeMillis() - begin);
        } finally {
            System.gc();
        }
    }

    private void write(VirtualMemoryBlock block) {
        byte[] array = new byte[block.size()];
        for (int i = 0; i < block.size(); i++) {
            array[i] = (byte) i;
        }
        block.write(array);

        byte[] read = new byte[block.size()];
        block.read(read);
    }

    private static VirtualMemory createFixedVirtualMemory() {
        return new FixedVirtualMemory(
                new ByteArrayStorage(SIZE),
                new LinkedVirtualMemoryTable(SIZE));
    }

    private static VirtualMemory createFixedBBVirtualMemory() {
        return new FixedVirtualMemory(
                new ByteBufferStorage(ByteBuffer.allocate(SIZE)),
                new LinkedVirtualMemoryTable(SIZE));
    }

    private static VirtualMemory createFixedFileVirtualMemory() {
        try {
            File file = new File("vm-performance.test");
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileChannel channel = randomAccessFile.getChannel();
            channel.force(true);

            ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, SIZE);

            VirtualMemory vm = new FixedVirtualMemory(
                    new ByteBufferStorage(buffer),
                    new LinkedVirtualMemoryTable(SIZE));

            vm.setFreeEventListener(new FreeEventListener() {
                public void onFree(VirtualMemory memory) {
                    try {
                        channel.close();
                        randomAccessFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return vm;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DynamicVirtualMemory createDynamicVirtualMemory() {
        int size = SIZE / 1000;
        return new DynamicVirtualMemory(
                new DynamicStorage(size, SIZE, size, ByteArrayStorageFactory.getInstance()),
                new LinkedVirtualMemoryTable(size));
    }

}
