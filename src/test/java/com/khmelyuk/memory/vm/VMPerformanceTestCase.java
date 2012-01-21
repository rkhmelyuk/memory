package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.vm.storage.ByteArrayStorage;
import com.khmelyuk.memory.vm.storage.ByteBufferStorage;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Test performance for different linked virtual memory tables
 *
 * @author Ruslan Khmelyuk
 */
public class VMPerformanceTestCase {

    static final int N = 5;
    static final int SIZE = 2 * Memory.MB;
    static final int COUNT_COEFF = 2000;

    @Test
    public void testInitPerformance() {
        testPerformance(createFixedVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createFixedVirtualMemory(), i);
        }

        //System.out.println("init: Avg. duration " + (total / N) + "ms");
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
        }
        finally {
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

    private static DynamicVirtualMemory createDynamicVirtualMemory() {
        int size = SIZE / 1000;
        return new DynamicVirtualMemory(size, SIZE, size,
                new LinkedVirtualMemoryTable(size));
    }

}
