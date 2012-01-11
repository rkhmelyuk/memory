package com.khmelyuk.memory.vm.table;

import org.junit.Test;

import com.khmelyuk.memory.Memory;

/**
 * Test performance for different linked virtual memory tables
 *
 * @author Ruslan Khmelyuk
 */
public class TablePerformanceTestCase {

    static final int N = 20;
    static final int SIZE = 2 * Memory.MB;
    static final int COUNT_COEFF = 2000;

    /*@Test
    public void testArrayTablePerformance() {
        long total = 0;

        for (int i = 0; i < N; i++) {
            total += testPerformance(new ArrayVirtualMemoryTable(SIZE), i);
        }

        System.out.println("ATP: Avg. duration " + (total / N) + "ms");
    }*/

    @Test
    public void testLinkedTablePerformance() {
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(new LinkedVirtualMemoryTable(SIZE), i);
        }

        System.out.println("LTP: Avg. duration " + (total / N) + "ms");
    }

    private long testPerformance(VirtualMemoryTable table, int n) {
        int max = COUNT_COEFF * (n + 5);
        int avgBlockSize = table.getFreeMemorySize() / max;

        Block[] blocks = new Block[max];
        long begin = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            blocks[i] = table.allocate(avgBlockSize - i * 10);
            if (i != 0) {
                free(table, blocks, i - 1);
            }
        }
        return (System.currentTimeMillis() - begin);
    }

    private void free(VirtualMemoryTable table, Block[] blocks, int n) {
        if (n > 2) {
            table.free(blocks[n - 2]);
        }
    }
}
