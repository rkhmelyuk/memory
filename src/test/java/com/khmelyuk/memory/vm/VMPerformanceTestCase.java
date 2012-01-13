package com.khmelyuk.memory.vm;

import org.junit.Test;

import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

import java.nio.ByteBuffer;

/**
 * Test performance for different linked virtual memory tables
 *
 * @author Ruslan Khmelyuk
 */
public class VMPerformanceTestCase {

    static final int N = 5;
    static final int SIZE = 10 * Memory.MB;
    static final int COUNT_COEFF = 2000;

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
        long avgRead = 0;
        long avgWrite = 0;
        long avgAllocation = 0;
        for (int i = 0; i < N; i++) {
            DynamicVirtualMemory vm = createDynamicVirtualMemory();
            total += testPerformance(vm, i);

            avgWrite += vm.avgWrite;
            avgRead += vm.avgRead;
            avgAllocation += vm.avgAllocation;
        }

        System.out.println("avg write " + (avgWrite / N) + "ns");
        System.out.println("avg read " + (avgRead / N) + "ns");
        System.out.println("avg alloc " + (avgAllocation / N) + "ns");

        System.out.println("DVM: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testByteBufferVMPerformance() {
        testPerformance(createByteBufferVirtualMemory(), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(createByteBufferVirtualMemory(), i);
        }

        System.out.println("BBVM: Avg. duration " + (total / N) + "ms");
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
        return (System.currentTimeMillis() - begin);
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

    private static DynamicVirtualMemory createDynamicVirtualMemory() {
        int size = SIZE / 10;
        return new DynamicVirtualMemory(size, SIZE, size,
                new LinkedVirtualMemoryTable(size));
    }

    private static VirtualMemory createByteBufferVirtualMemory() {
        return new ByteBufferVirtualMemory(
                ByteBuffer.allocate(SIZE),
                new LinkedVirtualMemoryTable(SIZE));
    }
}
