package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.Memory;
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

    static final int N = 1;
    static final int SIZE = 2 * Memory.MB;
    static final int COUNT_COEFF = 2000;

   /* @Test
    public void testFixed2VMPerformance() {
        testPerformance(createFixed2VirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createFixed2VirtualMemory(), i);
        }

        System.out.println("F2VM: Avg. duration " + (total / N) + "ms");
    }*/

    @Test
    public void testFixed21VMPerformance() {
        testPerformance(createFixed2VirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createFixed2VirtualMemory(), i);
        }

        System.out.println("F21VM: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testFixedVMPerformance() {
        testPerformance(createFixedVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createFixedVirtualMemory(), i);
        }

        System.out.println("FVM: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testDynamicVMPerformance() {
        testPerformance(createDynamicVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            DynamicVirtualMemory vm = createDynamicVirtualMemory();
            total += testPerformance(vm, i);
        }

        System.out.println("DVM: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testByteBufferVMPerformance() {
        long total = testPerformance(createByteBufferVirtualMemory(), 0);
        /*long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createByteBufferVirtualMemory(), i);
        }*/

        System.out.println("BBVM: Avg. duration " + (total / 1) + "ms");
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
        return new FixedVirtualMemory(SIZE,
                new LinkedVirtualMemoryTable(SIZE));
    }

    private static VirtualMemory createFixed2VirtualMemory() {
        return new Fixed2VirtualMemory(new ByteBufferStorage(ByteBuffer.allocate(SIZE)),
                new LinkedVirtualMemoryTable(SIZE));
    }

    private static DynamicVirtualMemory createDynamicVirtualMemory() {
        int size = SIZE / 1000;
        return new DynamicVirtualMemory(size, SIZE, size,
                new LinkedVirtualMemoryTable(size));
    }

    private static VirtualMemory createByteBufferVirtualMemory() {
        return new ByteBufferVirtualMemory(
                ByteBuffer.allocate(SIZE),
                new LinkedVirtualMemoryTable(SIZE));
    }
}
